(ns imapadmin.ldap
  (:require [clj-ldap.client :as ldap])
  (:require [clojure.string :as str])
  (:require [imapadmin.base64 :as base64]))

(defn extract_credentials [authorization]
  (let [pair (base64/decode (subs authorization 6))]
    (str/split pair #":")))

(defn ldap_connect [user password]
  (ldap/connect {
                 :host     "mail.ec2.esav.fi"
                 :bind-dn  user
                 :password password}))

(defn ldap_test [user password]
  (ldap/get (ldap_connect user password) user))


(defn authenticate [authorization]
  (let [[username password] (extract_credentials authorization)]
    (try
      (ldap_test username password)
      (catch Exception e false))))
