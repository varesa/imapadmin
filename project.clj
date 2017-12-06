(defproject imapadmin "0.1.0-SNAPSHOT"

  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler imapadmin.core/handler}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [liberator "0.13"]
                 [compojure "1.3.4"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-json "0.4.0"]
                 [org.clojars.pntblnk/clj-ldap "0.0.12"]
                 [ring-server "0.4.0"]
                 [org.clojure/data.codec "0.1.0"]]

  :main imapadmin.run)
