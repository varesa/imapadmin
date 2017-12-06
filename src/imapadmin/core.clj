(ns imapadmin.core
  (:require [liberator.core :refer [resource defresource]]
            [liberator.representation :refer [ring-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [compojure.core :refer [defroutes ANY]]
            [imapadmin.ldap :refer [authenticate get_domains]]))

(defn get_authz_header [ctx]
  (get-in ctx [:request :headers "authorization"]))

(defn authorized? [ctx]
  (try
    (let [authz (get_authz_header ctx)]
      (if (= authz "Basic Og==")
        ; Empty authentication
        [ false { :representation { :media-type "application/json"}}]
        ; Try to authenticate to LDAP
        [ (authenticate authz) { :representation { :media-type "application/json"}}]))
    (catch Exception e (.getMessage e))))

(defn auth_prompt [ctx]
  (ring-response {:status 401 :some "abc"}
                 {:headers {"WWW-Authenticate" "Basic"}}))

(defroutes app
           (ANY "/" [] (resource
                         :available-media-types ["application/json"]
                         :authorized? authorized?
                         :handle-unauthorized auth_prompt

                         :handle-ok "Ok"))

           (ANY "/domains" [] (resource
                                :available-media-types ["application/json"]
                                :authorized? authorized?
                                :handle-unauthorized auth_prompt

                                :handle-ok (fn [ctx] (get_domains (get_authz_header ctx)) ))))

(def handler
  (-> app
      wrap-keyword-params
      wrap-json-params
      wrap-json-response))