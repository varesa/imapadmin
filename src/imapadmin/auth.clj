(ns imapadmin.auth
  (:require [clj-ldap.client :as ldap])
  (:require [clojure.string :as str])
  (:require [imapadmin.base64 :as base64]))

(defn extract_credentials [authorization]
  (let [pair (base64/decode (subs authorization 6))]
    (str/split pair #":")))

(defn ldap_bind [user password]
  (def ldap-server (ldap/connect {
                                  :host "ipa.tre.esav.fi"
                                  :bind-dn (format "uid=%s,cn=users,cn=accounts,dc=esav,dc=fi" user)
                                  :password password}))
  (ldap/get ldap-server "uid=esa,cn=users,cn=accounts,dc=esav,dc=fi"))

(defn authenticate [authorization]
  (let [[username password] (extract_credentials authorization)]
    (println (str "Username: " username ", password: " password)))
  true)