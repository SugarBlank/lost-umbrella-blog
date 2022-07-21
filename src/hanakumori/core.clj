(ns hanakumori.core
  (:require [clojure.string :as str]
            [stasis.core :as stasis]
            [markdown.core :as md]
            [hiccup.core :refer [html]]
            [hiccup.element :refer [link-to]]
            [hiccup.page :refer [html5]]
            [clojure.edn :as edn]
            [babashka.fs :as fs]
            [clojure.java.io :as io]
            [clojure.walk :as walk]
            [net.cgrand.enlive-html :as enlive]))

(def source-dir "resources/")

(defn year [] (.format (java.text.SimpleDateFormat. "yyyy") (new java.util.Date)))

(def file-extensions #".*\.md$")

(defn header-footer
  [page]
  (html5 {:lang "en"}
         [:head
          [:title "hanakumori"]
          [:meta {:charset "utf-8"}]
          [:meta {:name "viewport"
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:type "text/css" :href "/css/style.css" :rel "stylesheet"}]
          [:body
           [:div {:class "header"}
            [:div {:class "name"}
             [:a {:href "/"} "home"]
             [:div {:class "header-right"}
              [:a {:href "posts-index.html"} "posts"]]]]
           page]
          [:footer {:role "contentinfo"}
           [:br
            "Copyright © " (year) " - Scouri &nbsp; | &nbsp;"
            [:span.credit "Powered by " [:a {:href "https://github.com/magnars/stasis"} "Stasis"]]]]]))

(defn get-css
  [directory]
  (stasis/slurp-directory directory #".*\.css$"))

(defn key-to-html
  [key]
  (str/replace key #".md" ".html"))

(defn metadata-to-links
  "build html list from a vec of metadata dicts"
  [m]
  (html [:ul (for [k m]
               [:li (link-to k)])]))

(defn convert-to-md
  [src]
  (let [data (stasis/slurp-directory src file-extensions)
        html-paths (map key-to-html (keys data))
        html-content (map md/md-to-html-string (vals data))]
    (zipmap html-paths html-content)))

(defn filter-post-topic
  [topic metadata]
  (let [tag-for-page (update-vals metadata :tags)]
    (into [] (for [[k v] tag-for-page :when (= v topic)] k))))

(defn parse-post
  "Parses the post into a map.
  The map contains the metadata included at the top of the post,
  a html :body, and other useful bits of extracted data
  
  file can be a string, a path or a file "

  [file]
  (when-let [[_ year month day name] (re-matches #"([0-9]{4})-([0-9]{2})-([0-9]{2})-(.*)\.md" (fs/file-name file))]
    (with-open [rdr (java.io.PushbackReader. (io/reader (fs/file file)))]
      (let [[uri] (re-matches #"(.*)\.md" (fs/file-name file))
            meta (edn/read rdr)
            raw-body (slurp rdr)
            html-body (md/md-to-html-string raw-body :footnotes? true)]
        (into {:uri (str (fs/absolutize (fs/path source-dir uri)))
               :date (str (java.time.LocalDate/of
                      (Integer/parseInt year)
                      (Integer/parseInt month)
                      (Integer/parseInt day)))
               :name name
               :raw-body raw-body
               :html-body html-body
               :tags []}
              meta)))))

(defn parse-posts
  [path]
  (->> (fs/list-dir path)
       sort
       (map parse-post)
       reverse))

(defn insert-links
  [page links]
  (-> page
      (enlive/sniptest [:div#pageListDiv]
                       (enlive/content links))))  

(defn md-to-html
  [v]
  (for [k v]
    (key-to-html k)))

(defn get-page [path]
  (let [page-map (stasis/slurp-directory path file-extensions)
        home-page (get page-map "/index.md")
        prog-home (get page-map "/posts-index.md")
        posts (apply dissoc page-map ["/index.md" "/posts-index.md"])
        fmt-posts (md-to-html(map #(str/replace % #"(?<!index)\.html$" "") (keys posts)))
        posts-info (remove nil? (parse-posts source-dir))
        pages (zipmap fmt-posts posts-info)]
    (println "Posts:" posts)
    (println "fmt-posts:" fmt-posts)
     {:home home-page
      :posts posts-info
      :prog-home prog-home
      :location (keys pages)
      :pages (zipmap fmt-posts (vals posts))
      :metadata pages}))

(defn format-links [page-map]
  (let [{home :home
         prog-home :prog-home
         location :location
         metadata :metadata
         ehhh :pages
         posts :posts} page-map
        css-map (get-css source-dir)        
        progposts (md-to-html(filter-post-topic "Programming" metadata))
        prog-links (insert-links prog-home (metadata-to-links progposts))]
    (stasis/merge-page-sources {:pages ehhh
                                :home {"/index.html" home}
                                :prog-home {"/posts-index.html" prog-links}})))

(defn format-pages
  [page]
  (let [html-keys (keys page)
        page-data (map header-footer (vals page))
        css (get-css source-dir)
        all-page-keys (keys page)
        all-pages (->> (vals page)
                       (map header-footer)
                       (zipmap all-page-keys))]
    (println "Keys:" all-page-keys)
    (stasis/merge-page-sources {:pages all-pages
                                :css css})
    ))

(defn map-web-assets
  [dir]
  (let [css-map (get-css source-dir)]
    
    (stasis/merge-page-sources {:css css-map
                                })))
(defn create-site []
  (-> (get-page source-dir)
      (format-links)
      (format-pages)))

(def server
  (stasis/serve-pages create-site))


