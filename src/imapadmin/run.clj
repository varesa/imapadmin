(ns imapadmin.run
  (:require [imapadmin.core :refer [handler]]))

(use 'ring.server.standalone)

(defn -main []
  (serve handler))
