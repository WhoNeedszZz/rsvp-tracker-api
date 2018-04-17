(ns rsvp-tracker-api.core
  (:gen-class)
  (:require
    [cheshire.core :as json]
    [compojure.core :refer [defroutes GET]]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
    [ring.util.response :refer [content-type response]]))

(def base-path "/api/v1")

(defroutes app
  (GET "/" []
    (-> (response (json/generate-string {:foo "bar"}))
       (content-type "application/json"))))

(def handler
  (-> app
    (wrap-cors :access-control-allow-origin [#"http://localhost:3000"]
               :access-control-allow-methods [:get :post :put :delete])
    (wrap-defaults api-defaults)))
