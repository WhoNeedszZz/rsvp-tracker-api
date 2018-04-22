(ns rsvp-tracker-api.event
  (:gen-class)
  (:require
    [cheshire.core :refer [generate-string]]
    [monger.collection :refer [ensure-index find-map-by-id insert remove-by-id update-by-id]]
    [monger.core :refer [connect get-db]]
    [monger.joda-time]
    [monger.json]
    [rsvp-tracker-api.util :refer [gen-links parse-json]])
  (:import
    [org.bson.types ObjectId]))

(def db-name "rsvp-tracker")

(def collection "events")

(def ttl-secs 86400)

(def error-create (generate-string {:error "Failed to create event"}))

(def error-update (generate-string {:error "Failed to update event"}))

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
          (do
            (ensure-index db collection (array-map :dateEnd 1) {:expireAfterSeconds ttl-secs})
            (generate-string data))
          error-create))
      error-create)))

(defn get-event
  "Retrieves a specific event given by id"
  [id]
  (let [conn (connect)
        db (get-db conn db-name)]
    (generate-string (find-map-by-id db collection (ObjectId. id)))))

(defn update-event
  "Updates a specific event given by id"
  [id request]
  (let [conn (connect)
        db (get-db conn db-name)
        data (parse-json request)
        oid (ObjectId. id)
        update (update-by-id db collection oid data)]
    (if (.wasAcknowledged update)
      (get-event id)
      error-update)))

(defn delete-event
  "Deletes a specific event given by id"
  [id]
  (let [conn (connect)
        db (get-db conn db-name)
        oid (ObjectId. id)
        delete (remove-by-id db collection oid)]
    (if (.wasAcknowledged delete)
      (generate-string {:completed true})
      (generate-string {:completed false}))))
