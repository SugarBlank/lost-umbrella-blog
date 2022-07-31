(ns akaiyuki.misc
  (:require [clojure.string :as str]))

(def source-dir "resources/")

(def public-dir "build/")

(defn year [] (.format (java.text.SimpleDateFormat. "yyyy") (new java.util.Date)))

(def file-extensions #".*\.md$")

(defn key-to-html
  [key]
  (str/replace key #".md" ".html"))