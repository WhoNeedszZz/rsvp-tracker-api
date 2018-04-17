(ns rsvp-tracker-api.core-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [ring.mock.request :as mock]
    [rsvp-tracker-api.core :refer [handler]]))

(deftest api-base
  (testing "Initial bogus JSON data response from /"
    (is (= (handler (mock/request :get "/"))
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body "{\"foo\":\"bar\"}"}))))
