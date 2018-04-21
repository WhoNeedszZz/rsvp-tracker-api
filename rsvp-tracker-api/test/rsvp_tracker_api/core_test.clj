(ns rsvp-tracker-api.core-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [cheshire.core :refer [generate-string]]
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

(def create-error (generate-string {:error "Failed to create event"}))

(deftest api-base
  (testing "Initial bogus JSON data response from /"
    (is (= (handler (request :get "/"))
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body (generate-string {:foo :bar})}))))

(deftest create-event-valid-data
  (testing "Create an event at /api/v1/events"
    (is (not= (handler (-> (request :post events-endpoint)
                           (json-body initial-event)))
              {:status 400
               :headers {"Content-Type" "application/json"}
               :body create-error}))))

(deftest create-event-invalid-data
  (testing "Fail to create an event from invalid JSON"
    (is (= (handler (-> (request :post events-endpoint)
                        (json-body "foo")))
           {:status 400
            :headers {"Content-Type" "application/json"}
            :body create-error}))))
