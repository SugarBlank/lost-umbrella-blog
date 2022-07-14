(defproject hanakumori "0.1.0-SNAPSHOT"
  :description "Blog"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [stasis "2.5.1"]
                 [markdown-clj "1.11.1"]
                 [hiccup "1.0.5"]
                 [ring "1.9.5"]
                 [optimus "2022-02-13"]
                 [enlive "1.1.6"]]
  :ring {:handler hanakumori.core/server}
  :profiles {:dev {:plugins [[lein-ring "0.12.5"]]}}
  :repl-options {:init-ns hanakumori.core})
