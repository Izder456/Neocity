(defproject srcerizder-site "b1.0"
  :description "My Neocity Page"
  :url "https://izder456.neocities.org"
  :license {:name "WTFPL Version 2 (Modified)"
            :url "https://www.wtfpl.net/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [stasis "2023.06.03"]
                 [ring "1.10.0"]
                 [garden "1.3.10"]
                 [hiccup "2.0.0-RC1"]
                 [optimus "2023-02-08"]]
  :ring {:handler srcerizder-site.web/app}
  :aliases {"build-site" ["run" "-m" "srcerizder-site.web/export"]
            "clean-site" ["run" "-m" "srcerizder-site.web/clean"]
            "run-site" ["ring", "server"]}
  :profiles {:dev {:plugins [[lein-ring "0.12.6"]]}})
