// Package server /* for setting up a server */
package server

import (
	service "backend/pkg/Service"
	"github.com/gorilla/mux"
)

// MyHandler defines the routes, returns router
func MyHandler() *mux.Router {
	srv := service.NewService()
	router := mux.NewRouter()

	userRouter := router.PathPrefix("/getUsers").Subrouter()
	userRouter.HandleFunc("", srv.GetAllUsers).Methods("GET")

	//router.HandleFunc("/getUsers", srv.GetAllUsers).Methods("GET")
	router.HandleFunc("/createUser", srv.CreateUser).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/getTags", srv.GetAllTags).Methods("GET")
	router.HandleFunc("/{user_id:[0-9]+}/getNotes", srv.GetNotes).Methods("POST")
	router.HandleFunc("/{user_id:[0-9]+}/addNote", srv.AddNote).Methods("POST")
	router.Handle("/", router)

	return router
}
