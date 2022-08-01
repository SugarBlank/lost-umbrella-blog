(ns akaiyuki.pages
  (:require [akaiyuki.html :as html]
            [stasis.core :as stasis]
            [markdown.core :as md]
            [clojure.string :as str]
            [akaiyuki.misc :refer [file-extensions key-to-html source-dir]]
            [akaiyuki.metadata :as meta]
            [akaiyuki.parsing :as parse]))

(defn get-css
  [src]
  (stasis/slurp-directory src #".*\.css$"))

(defn get-page 
  [path]
  (let [page-map (stasis/slurp-directory path file-extensions)
        home-page (md/md-to-html-string (get page-map "/index.md"))
        posts-home (md/md-to-html-string (get page-map "/posts-index.md"))
        posts (apply dissoc page-map ["/index.md" "/posts-index.md" "/programming-index.md" "/electronics-index.md"])
        fmt-posts (meta/md-to-html (map #(str/replace % #"(?<!index)\.html$" "") (keys posts)))
        posts-info (remove nil? (parse/parse-posts source-dir))
        html-paths (map key-to-html (keys posts))
        html-content (map #(md/md-to-html-string % :heading-anchors true) (vals posts))
        pages (zipmap fmt-posts posts-info)]
    {:home home-page
     :posts-home posts-home
     :pages (zipmap html-paths html-content)
     :metadata pages}))

(defn format-links 
  [page-map]
  (let [{home :home
         posts-home :posts-home
         metadata :metadata
         pages :pages} page-map
        prog-links (html/create-posts "Programming" metadata)
        electronics-links (html/create-posts "Electronics" metadata)
        combined-links (html/insert-links posts-home (str prog-links electronics-links))]
    (stasis/merge-page-sources {:pages pages
                                :home {"/index.html" home}
                                :posts-home {"/posts-index.html" combined-links}
                                :prog-home {"/programming-index.html" prog-links}
                                :elec-home {"/electronics-index.html" electronics-links}})))

(defn format-pages
  [page]
  (let [css (get-css source-dir)
        all-page-keys (keys page)
        all-pages (->> (vals page)
                       (map html/header-footer)
                       (zipmap all-page-keys))]
    (stasis/merge-page-sources {:pages all-pages
                                :css css})))
