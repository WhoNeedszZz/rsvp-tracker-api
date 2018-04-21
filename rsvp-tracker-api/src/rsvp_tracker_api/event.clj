(ns rsvp-tracker-api.event
  (:gen-class)
  (:require
    [cheshire.core :refer [generate-string]]
    [monger.collection :refer [find-maps insert]]
    [monger.core :refer [connect get-db]]
    [monger.joda-time]
    [monger.json]
    [rsvp-tracker-api.util :refer [gen-links parse-json]])
  (:import
    [org.bson.types ObjectId]))

(def db-name "rsvp-tracker")

(def collection "events")

(def error-create (generate-string {:error "Failed to create event"}))

(defn create-event
  "Insert and return the event contained within the given context into the db"
  [request]
  (let [conn (connect)
        db (get-db conn db-name)
        id (ObjectId.)
        preData (parse-json request)
        data (assoc preData :_id id :links (gen-links request id))]
    (if (not= 2 (count (keys data)))
      (let [event (insert db collection data)]
        (if (.wasAcknowledged event)
          (generate-string data)
          error-create))
      error-create)))

(defn get-events
  "Retrieves all events from db"
  []
  (let [conn (connect)
        db (get-db conn db-name)]
    (find-maps db collection)))
