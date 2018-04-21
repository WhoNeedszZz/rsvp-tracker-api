(ns rsvp-tracker-api.util
  (:gen-class)
  (:require
    [cheshire.core :refer [parse-string]]
    [clj-time.local :as ltime]
    [clojure.java.io :as io]
    [clojure.string :as str]))

(defn body-as-string
  "Returns the body of the quest as a string"
  [request]
  (if-let [body (:body request)]
    (condp instance? body
      java.lang.String body
      (slurp (io/reader body)))))

(defn date-str->date
  "Converts the date strings in data into dates for the db"
  [data]
  (assoc data :dateStart (ltime/to-local-date-time (:dateStart data))
              :dateEnd (ltime/to-local-date-time (:dateEnd data))))

(defn parse-json
  "Parses the JSON from the request data"
  [request]
  (when (#{:post :put} (:request-method request))
    (try
      (if-let [body (body-as-string request)]
        (date-str->date (parse-string body true))
        {:message "No body"})
      (catch Exception e
        (.printStackTrace e)))))

(defn get-res-location
  "Generates the url of the given resource"
  [request id]
  (format "%s://%s:%s%s/%s"
          (name (:scheme request))
          (:server-name request)
          (:server-port request)
          (:uri request)
          (str id)))

(defn gen-links
  "Geenerate HATEOAS links for the given resource"
  [request id]
  (let [self (get-res-location request id)
        events (str/replace self (format "/%s" id) "")]
    (assoc {} :self self :events events)))
