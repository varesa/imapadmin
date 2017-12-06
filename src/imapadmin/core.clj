(ns imapadmin.core
  (:require [liberator.core :refer [resource defresource]]
            [liberator.representation :refer [ring-response]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]

            [imapadmin.ldap :refer [authenticate]]))


(defn authorized? [ctx]
  (try
    (let [authz (get-in ctx [:request :headers "authorization"])]
      (if (= authz "Basic Og==")
        ; Empty authentication
        false
        ; Try to authenticate to LDAP
        (authenticate authz)))
    (catch Exception e (.getMessage e))))


(defroutes app
           (ANY "/" [] (resource
                         :available-media-types ["application/json"]

                         :authorized? (fn [ctx] [(authorized? ctx) { :representation { :media-type "application/json"}}])                 ;(fn [ctx] (authorized? ctx))

                         :allowed? [true { :representation { :media-type "application/json"}}]

                         :handle-unauthorized (fn [ctx]
                                                (ring-response {:status 401 :some "abc"}
                                                               {:headers {"WWW-Authenticate" "Basic"}}))
                         :handle-ok "Ok"))

           (ANY "/domains" [] (resource
                                :available-media-types ["application/json"]

                                :handle-ok "Domains")))


(def handler
  (-> app
      wrap-params))