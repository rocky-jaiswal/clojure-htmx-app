(ns htmx-app.services.user-test
  (:require [clojure.test           :refer [deftest is testing]]
            [buddy.hashers          :as hashers]
            [htmx-app.services.user :as svc]))

(deftest verify-password
  (let [user {:users/password (hashers/derive "correct-password")}]
    (testing "returns true for correct password"
      (is (true? (svc/verify-password user "correct-password"))))
    (testing "returns false for wrong password"
      (is (false? (svc/verify-password user "wrong-password"))))))
