(ns rsvp-tracker-api.core-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [cheshire.core :refer [generate-string parse-string]]
    [ring.mock.request :refer [json-body request]]
    [rsvp-tracker-api.core :refer [handler]]))

(def events-endpoint "/api/v1/events")

(def initial-event {"title" "Test Event 1"
                    "location" "Lafayette, LA"
                    "dateStart" "2017-11-21T00:00:00.000Z"
                    "dateEnd" "2017-11-21T04:00:00.000Z"
                    "description" "Live it up"
                    "organizers" ["fex@bar.com"]
                    "invited" ["bob@foo.com" "sarah@foo.com"]
                    "tentative" []
                    "accepted" []
                    "declined" []})

(def event2 {"title" "Test Event 2"
             "location" "Scott, LA"
             "dateStart" "2017-11-27T23:00:00.000Z"
             "dateEnd" "2017-11-28T02:00:00.000Z"
             "description" "Another round"
             "organizers" ["john@foo.com"]
             "invited" ["adam@foo.com" "sarah@foo.com"]
             "tentative" []
             "accepted" []
             "declined" []})

(def create-error (generate-string {:error "Failed to create event"}))

(def view-empty (generate-string []))

(def err-id-not-found (generate-string {:error "Event with given ID does not exist"}))\

(defn get-first-event
  "Get first event created this test session (id is random)"
  []
  (let [response (handler (request :get events-endpoint))]
    (first (parse-string (:body response)))))

(deftest api-base
  (testing "Initial bogus JSON data response from /"
    (is (= (handler (request :get "/"))
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body (generate-string {:foo :bar})}))))

(deftest view-all-events-empty
  (testing "View all events when events is empty"
    (is (= (handler (request :get events-endpoint))
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body view-empty}))))

(deftest create-event-valid-data
  (testing "Create an event at /api/v1/events"
    (is (let [response (handler (-> (request :post events-endpoint)
                                    (json-body initial-event)))]
          (= (:status response)
             201)))))

(deftest create-event-invalid-data
  (testing "Fail to create an event from invalid JSON"
    (is (= (handler (-> (request :post events-endpoint)
                        (json-body "foo")))
           {:status 400
            :headers {"Content-Type" "application/json"}
            :body create-error}))))

(deftest view-all-events
  (testing "View all events when there is an event"
    (is (let [response (handler (request :get events-endpoint))]
          (and (= (:status response)
                  200)
               (not= (:body response)
                     view-empty))))))

(deftest view-one-event-exists
  (testing "View an event by id (exists)"
    (is (let [event (get-first-event)
              id (get-in event ["_id"])
              response (handler (request :get (str events-endpoint (format "/%s" id))))]
          (= (:status response)
             200)))))

(deftest view-one-event-invalid
  (testing "View and event by id (doesn't exist)"
    (is (let [response (handler (request :get (str events-endpoint (format "/%s" 111111111111111111111111))))]
          (= (:body response)
             "null")))))
