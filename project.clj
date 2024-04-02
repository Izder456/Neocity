(defproject srcerizder-site "1.0"
  :description "My Neocity Page"
  :url "https://izder456.neocities.org"
  :license {:name "0BSD"
            :url "https://opensource.org/license/0bsd"}
  :plugins [[dev.weavejester/lein-cljfmt "0.11.2"]]
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [stasis "2023.11.21"]
                 [ring "1.11.0-RC2"]
                 [garden "1.3.10"]
                 [hiccup "2.0.0-RC2"]
                 [optimus "2023.11.21"]]
  :ring {:handler srcerizder-site.web/app}
  :aliases {"build-site" ["run" "-m" "srcerizder-site.web/export"]
            "clean-site" ["run" "-m" "srcerizder-site.web/clean"]
            "run-site" ["ring", "server"]}
  :repl-options {:init-ns srcerizder-site.web
                 :init (use 'srcerizder-site.web :reload)}
  :profiles {:dev {:plugins [[lein-ring "0.12.6"]]}})
