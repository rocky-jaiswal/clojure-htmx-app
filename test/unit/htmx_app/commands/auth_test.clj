(ns htmx-app.commands.auth-test
  (:require [clojure.test           :refer [deftest is testing]]
            [htmx-app.commands.auth :as cmd]
            [htmx-app.services.user :as user-svc]))

(def ^:private existing-user
  {:users/id 1 :users/email "taken@example.com" :users/role "user"
   :users/password (buddy.hashers/derive "correct-pw")})

(deftest register!
  (testing "mismatched passwords"
    (is (= "Passwords do not match"
           (:error (cmd/register! nil {:email "a@b.com"
                                       :password "pass1234"
                                       :confirm-password "different"})))))

  (testing "password too short"
    (is (= "Password must be at least 8 characters"
           (:error (cmd/register! nil {:email "a@b.com"
                                       :password "short"
                                       :confirm-password "short"})))))

  (testing "blank email"
    (is (some? (:error (cmd/register! nil {:email ""
                                           :password "pass1234"
                                           :confirm-password "pass1234"})))))

  (testing "email already taken"
    (with-redefs [user-svc/find-by-email (fn [_ _] existing-user)]
      (is (= "Email already registered"
             (:error (cmd/register! nil {:email "taken@example.com"
                                         :password "pass1234"
                                         :confirm-password "pass1234"}))))))

  (testing "successful registration returns normalized user"
    (with-redefs [user-svc/find-by-email (fn [_ _] nil)
                  user-svc/create!       (fn [_ _] {:users/id 2
                                                     :users/email "new@example.com"
                                                     :users/role "user"})]
      (let [result (cmd/register! nil {:email "new@example.com"
                                       :password "pass1234"
                                       :confirm-password "pass1234"})]
        (is (= 2 (get-in result [:ok :id])))
        (is (= "new@example.com" (get-in result [:ok :email])))))))

(deftest login
  (testing "wrong password"
    (with-redefs [user-svc/find-by-email  (fn [_ _] existing-user)
                  user-svc/verify-password (fn [_ _] false)]
      (is (= "Invalid email or password"
             (:error (cmd/login nil {:email "taken@example.com" :password "wrong"}))))))

  (testing "unknown email"
    (with-redefs [user-svc/find-by-email (fn [_ _] nil)]
      (is (= "Invalid email or password"
             (:error (cmd/login nil {:email "nobody@example.com" :password "pass"}))))))

  (testing "successful login returns normalized user"
    (with-redefs [user-svc/find-by-email  (fn [_ _] existing-user)
                  user-svc/verify-password (fn [_ _] true)]
      (let [result (cmd/login nil {:email "taken@example.com" :password "correct-pw"})]
        (is (= 1 (get-in result [:ok :id])))
        (is (= "taken@example.com" (get-in result [:ok :email])))))))
