(ns akaiyuki.html
  (:require [akaiyuki.metadata :as meta]
            [net.cgrand.enlive-html :as enlive]
            [clojure.string :as str]
            [hiccup.page :refer [html5]]
            [akaiyuki.misc :refer [year]]))

(defn header-footer
  [page]
  (html5 {:lang "en"}
         [:head
          [:title "akaiyuki"]
          [:meta {:charset "utf-8"}]
          [:meta {:name "viewport"
                  :content "width=device-width, initial-scale=1.0"}]
          [:link {:type "text/css" :href "/public/css/style.css" :rel "stylesheet"}]
          [:body
           [:div {:class "header"}
            [:div {:class "name"}
             [:a {:href "/"} "akaiyuki"]
             [:div {:class "header-right"}
              [:a {:href "posts-index.html"} "posts"]]]]
           page]
          [:footer {:role "contentinfo"}
           [:br
            "Copyright © " (year) " | "
            [:span.credit 
             [:a {:href "https://github.com/scourii/akaiyuki-blog"} "Source"]]]]]))

(defn insert-links
  [page links]
  (-> page
      (enlive/sniptest [:div#pageListDiv]
                       (enlive/html-content links))))

(defn- create-title 
  [topic]
  (let [link (str (str/lower-case topic) "-index.html")]
    [:a {:href link} topic]))

(defn create-posts
  [topic metadata]
  (let [topic-title (create-title topic)
        topic-info (meta/filter-post-topic topic metadata)
        topic-posts (meta/metadata-to-links topic-info topic-title)]
    insert-links topic-posts))
