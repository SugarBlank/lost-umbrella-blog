(ns hanakumori.misc
  (:require [clojure.string :as str]))

(def source-dir "resources/")

(defn year [] (.format (java.text.SimpleDateFormat. "yyyy") (new java.util.Date)))

(def file-extensions #".*\.md$")

(defn key-to-html
  [key]
  (str/replace key #".md" ".html"))