(ns hanakumori.parsing 
  (:require [babashka.fs :as fs]
            [markdown.core :as md]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [hanakumori.misc :refer [source-dir key-to-html]]))

(defn parse-post
  [file]
  (when-let [[_ year month day name] (re-matches #"([0-9]{4})-([0-9]{2})-([0-9]{2})-(.*)\.md" (fs/file-name file))]
    (with-open [rdr (java.io.PushbackReader. (io/reader (fs/file file)))]
      (let [[uri] (re-matches #"(.*)\.md" (fs/file-name file))
            meta (edn/read rdr)
            raw-body (slurp rdr)
            html-body (md/md-to-html-string raw-body :heading-anchors true)]
        (into {:uri (key-to-html (fs/file-name (fs/path source-dir uri)))
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
