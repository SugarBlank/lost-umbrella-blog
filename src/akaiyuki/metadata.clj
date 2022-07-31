(ns akaiyuki.metadata
  (:require [akaiyuki.misc :refer [key-to-html]]
            [hiccup.page :refer [html5]]
            [hiccup.element :refer [link-to]]))

(defn md-to-html
  [v]
  (for [k v]
    (key-to-html k)))

(defn metadata-to-links
  "build html list from a vec of metadata dicts"
  [m title]
  (html5 [:h3 title]
         [:ul (for [k m]
                [:li (link-to (:uri k) (:title k))
                 (str "<br><em> Published: <b>" (:date k) "<br></b></em>")])]))

(defn filter-post-topic
  [topic metadata]
  (let [tag-for-page (update-vals metadata #(dissoc % :raw-body ))]
    (into [] (for [[_ v] tag-for-page :when (= (:tags v) topic)] v))))