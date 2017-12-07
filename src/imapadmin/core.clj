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

(def resource-defaults
  {:available-media-types ["application/json"]
   :authorized? authorized?
   :handle-unauthorized auth_prompt})

(defn handler-domains [ctx]
  (def domains [])
  (println "Handling")
  (let [ldap_domains (get_domains (get_authz_header ctx))]
    (println ldap_domains)
    (doseq [domain ldap_domains]
      (def domains (conj domains (get-in domain [:dc])))))
  domains)

(defresource resource-domain [dom]
             resource-defaults
             :handle-ok (fn [ctx] (format "This is domain %s" dom)))

(defroutes app
           (ANY "/" [] (resource
                         resource-defaults
                         :handle-ok "Ok"))

           (ANY "/domains" [] (resource
                                resource-defaults
                                :handle-ok handler-domains))

           (ANY "/domains/:d" [d] (resource-domain d)))



(def handler
  (-> app
      wrap-keyword-params
      wrap-json-params
      wrap-json-response))