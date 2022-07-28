(ns hanakumori.core
  (:require [hanakumori.pages :as pages]
            [optimus.assets :as assets]
            [stasis.core :as stasis]
            [hanakumori.misc :refer [public-dir source-dir]]
            [optimus.prime :as optimus]
            [optimus.optimizations :as optimizations]
            [optimus.export]
            [optimus.strategies :refer [serve-live-assets]]
            [ring.middleware.resource :refer [wrap-resource]]))

(defn create-site []
  (-> (pages/get-page source-dir)
      pages/format-links
      pages/format-pages))

(defn get-assets
  []
  (assets/load-assets "public" [#".*\.jpg"]))

(def app
  (optimus/wrap
   (stasis/serve-pages create-site)
   get-assets
   optimizations/all
   serve-live-assets))


(defn export []
  (let [assets (optimizations/all get-assets {})
        pages create-site]
    (stasis/empty-directory! public-dir)
    (optimus.export/save-assets assets public-dir)
    (stasis/export-pages pages public-dir {:optimus-assets assets})))
