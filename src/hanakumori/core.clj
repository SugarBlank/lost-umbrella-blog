(ns hanakumori.core
  (:require [hanakumori.pages :as pages]
            [stasis.core :as stasis]
            [hanakumori.misc :refer [source-dir]]))

(defn create-site []
  (-> (pages/get-page source-dir)
      pages/format-links
      pages/format-pages))

(def server
  (stasis/serve-pages create-site))

(defn export
  "main export function for static site."
  []
  (stasis/export-pages (create-site) "public"))
