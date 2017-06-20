(ns imapadmin.base64
  (:require [clojure.data.codec.base64 :as b64]))

;; Wrappers for a cleaner bytes/string conversion

(defn toBytes [string]
  (.getBytes string))

(defn toString [bytes]
  (String. bytes))

;; Encode / Decode Base64

(defn encode [text]
  (let [bytes (toBytes text)]
    (toString (b64/encode bytes))))

(defn decode [text]
  (let [bytes (toBytes text)]
    (toString (b64/decode bytes))))