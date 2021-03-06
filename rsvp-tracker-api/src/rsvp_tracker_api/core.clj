(ns rsvp-tracker-api.core
  (:gen-class)
  (:require
    [cheshire.core :refer [generate-string]]
    [compojure.core :refer [defroutes DELETE GET POST PUT]]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
    [ring.util.response :refer [content-type response status]]
    [rsvp-tracker-api.event :refer [create-event delete-event get-event update-event]]))

(def base-path "/api/v1")

(defn event-created?
  "Adds a Ring HTTP status depending on the success of creating an event"
  [response]
  (if (not= (:body response) (generate-string {:error "Failed to create event"}))
    (status response 201)
    (status response 400)))

(defn event-deleted?
  "Adds a Ring HTTP status depending on the success of deleting an event"
  [response]
  (if (= (:body response) (generate-string {:completed true}))
    (status response 204)
    (status response 400)))

(defroutes app
  (GET "/" []
    (-> (response (generate-string {:foo "bar"}))
        (content-type "application/json")))
  (POST (str base-path "/events") request
    (-> (response (create-event request))
        (content-type "application/json")
        (event-created?)))
  (GET (str base-path "/events/:id") [id]
    (-> (response (get-event id))
        (content-type "application/json")))
  (PUT (str base-path "/events/:id") [id :as request]
    (-> (response (update-event id request))
        (content-type "application/json")))
  (DELETE (str base-path "/events/:id") [id]
    (-> (response (delete-event id))
        (content-type "application/json")
        (event-deleted?))))

(def handler
  (-> app
    (wrap-cors :access-control-allow-origin [#"http://localhost:3000"]
               :access-control-allow-methods [:get :post :put :delete])
    (wrap-defaults api-defaults)))
