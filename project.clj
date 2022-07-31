(defproject akaiyuki "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.0"]
                 [stasis "2.5.1"]
                 [markdown-clj "1.11.1"]
                 [hiccup "1.0.5"]
                 [ring "1.9.5"]
                 [digest "1.4.9"]
                 [optimus "2022-02-13"]
                 [enlive "1.1.6"]
                 [babashka/fs "0.1.6"]
                 [clygments "2.0.2"]]
  :ring {:handler akaiyuki.core/app}
  :profiles {:dev {:plugins [[lein-ring "0.12.6"]]}}
  :repl-options {:init-ns akaiyuki.core}
  :aliases {"build-site" ["run" "-m" "akaiyuki.core/export"]})
