(ns imapadmin.core
  (:require [liberator.core :refer [resource defresource]]
            [liberator.representation :refer [ring-response]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]
            [imapadmin.auth :refer [authenticate]]))


(defn authorized? [ctx]
  (contains?
    (get-in ctx [:request :headers])
    "authorization"))


(defroutes app
           (ANY "/" [] (resource
                         :available-media-types ["application/json"]

                         :authorized? (fn [ctx] [(authorized? ctx) {:representation {:media-type "text/plain"}}])

                         :allowed? (fn [ctx] (authenticate (get-in ctx [:request :headers "authorization"])))

                         :handle-unauthorized (fn [ctx]
                                                (ring-response {:status 401 :some "abc"}
                                                               {:headers {"WWW-Authenticate" "Basic"}}))

                         :handle-ok "Ok")))

(def handler
  (-> app
      wrap-params))