(ns hanakumori.routes.home
  (:require
   [hanakumori.layout :as layout]
   [clojure.java.io :as io]
   [hanakumori.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn posts-page [request]
  (layout/render request "posts.html" {:docs (-> "docs/posts.md" io/resource slurp)}))
  (defn lily58-post [request]
    (layout/render request "lily58.html" {:docs (-> "docs/lily58.md" io/resource slurp)}))
(defn home-routes []
  [ "" 
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/posts" {:get posts-page}]
   ["/lily58" {:get lily58-post}]])

