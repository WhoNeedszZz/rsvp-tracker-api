(defproject rsvp-tracker-api "0.0.1"
  :description "A ReSTful API for an RSVP tracking service written in Clojure. Persistence is provided by Mongodb."
  :license {:name "GNU General Public License v3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.txt"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [cheshire "5.8.0"]
                 [clj-time "0.14.3"]
                 [compojure "1.6.1"]
                 [ring/ring-core "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [ring-cors "0.1.12"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler rsvp-tracker-api.core/handler
         :port 8080}
  :main ^:skip-aot rsvp-tracker-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[proto-repl "0.3.1"]
                                  [ring/ring-mock "0.3.2"]]}})
