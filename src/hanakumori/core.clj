(ns hanakumori.core
  (:require [clojure.string :as str]
            [stasis.core :as stasis]
            [markdown.core :as md]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]
            [hiccup.element :as element]
            [net.cgrand.enlive-html :as enlive]
            [clojure.edn :as edn]))

(def source-dir "resources/")

(defn year [] (.format (java.text.SimpleDateFormat. "yyyy") (new java.util.Date)))

(def file-extensions #".*\.(md|edn)$")

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
                     [:a {:href "/posts"} "posts"]]]]
                  page]
                 [:footer {:role "contentinfo"}
                  [:p
                   "Copyright © " (year) " - Scouri &nbsp; | &nbsp;"
                   [:span.credit "Powered by " [:a {:href "https://github.com/magnars/stasis"} "Stasis"]]]]]))

(defn insert-links [page links]
  (-> page
      (enlive/sniptest [:div#pageListDiv]
                       (enlive/html-content links))))
(defn key-to-html
  [key]
  (str/replace key #".md" ".html"))

(defn get-css
  [directory]
  (stasis/slurp-directory directory #".*\.css$"))

(defn parse-edn [text]
  (edn/read-string
   (apply str
          (enlive/select (enlive/html-snippet text) [:#edn enlive/text-node]))))

(defn metadata-to-links [meta]
  (html [:u (for [k meta]
              [:li (element/link-to (get k :path) (get k :title))
               (str "<em> Published: " (get k :date) "</em>")])]))

(defn filter-topic 
  [topic v]
  (filter #(= topic (:topic %)) v))

(defn convert-to-md
  [src]
  (let [data (stasis/slurp-directory src file-extensions)
        
        html-paths (map key-to-html (keys data))
        html-content (map md/md-to-html-string (vals data))]
    (zipmap html-paths html-content)))

(defn format-pages
  [page]
  (let [html-keys (keys page)
        page-data (map header-footer (vals page))]
    (zipmap html-keys page-data)))

(defn format-links [page-map]
  (let [{prog-home :prog-index
        pages :pages
        metadata :metadata} page-map
        prog-links (->> metadata
                        (filter-topic "programming")
                        (metadata-to-links))
        prog (insert-links prog-home prog-links)]
    (stasis/merge-page-sources {:pages pages
                                :prog-home {"/Programming/index.md" prog}})))

(defn map-web-assets
  [dir]
  (let [page-map (format-pages (convert-to-md dir))
        css-map (get-css dir)]
    (stasis/merge-page-sources {:css css-map
                                :pages page-map})))
(defn create-site [dir]
  (-> (map-web-assets dir)
      (format-links)))

(def server
  (stasis/serve-pages create-site source-dir))
