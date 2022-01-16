(ns lostumbrella.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[lostumbrella started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[lostumbrella has shut down successfully]=-"))
   :middleware identity})
