// Package service /*
package service

import (
	"encoding/json"
	"github.com/gorilla/mux"
	log "github.com/sirupsen/logrus"
	"io/ioutil"
	"math/rand"
	"net/http"
	"strconv"
	u "user/pkg/user"
)

// struct to parse the request from user
type request struct {
	UserID int32  `json:"target_id"`
	TagID  int32  `json:"tag_id"`
	Note   string `json:"note"`
}

type service struct {
	store map[int32]*u.User
}

func NewService() *service {
	return &service{make(map[int32]*u.User)}
}

// Contains check if the map Contains the specific user
func (s *service) Contains(u *u.User) bool {
	for _, i := range s.store {
		if i == u {
			return true
		}
	}
	return false
}

// Id generator
func (s *service) newId() int32 {
	var id int32
	// It's limited to 2^31 + 1
	// Wanted to use hash, but then thought it would be too much
	for s.store[id+1] != nil {
		id = rand.Int31()
	}
	return id + 1
}

// GetAllUsers func to return all of the users in the map
func (s *service) GetAllUsers(w http.ResponseWriter, r *http.Request) {
	// error checking
	if r.Method != "GET" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	for id, user := range s.store {
		w.Write([]byte("id: " + strconv.Itoa(int(id)) + "\nUser: " + user.ToString() + "\n"))
	}
}

func (s *service) CreateUser(w http.ResponseWriter, r *http.Request) {
	// error and requirements checking
	if r.Method != "GET" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	// wanted to make post one
	if r.Method == "POST" {
		content, err := ioutil.ReadAll(r.Body)
		defer r.Body.Close()
		if err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			log.Error("Cannot read the data from request")
			w.Write([]byte(err.Error()))
			return
		}

		tmpUser := u.NewUser()

		if err := json.Unmarshal(content, &tmpUser); err != nil {
			w.WriteHeader(http.StatusInternalServerError)
			w.Write([]byte(err.Error()))
			log.Error("Cannot parse data from json")
			return
		}
	}
	tmpUser := u.NewUser()
	id := s.newId()
	s.store[id] = tmpUser

	log.Info("New user: ", id)
	w.WriteHeader(http.StatusCreated)
	w.Write([]byte("new User was created\nid:" + strconv.Itoa(int(id)) + "\n"))
}

func (s *service) AddTag(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	vars := mux.Vars(r)
	tmp, _ := strconv.Atoi(vars["user_id"])
	userId := int32(tmp)

	if _, ok := s.store[userId]; !ok {
		w.Write([]byte("No such user"))
		return
	}

	id := s.store[userId].NewTag()
	w.WriteHeader(http.StatusCreated)
	w.Write([]byte("New Tag id:" + strconv.Itoa(int(id)) + " was created\n"))
}

func (s *service) GetNotes(w http.ResponseWriter, r *http.Request) {
	if r.Method != "GET" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}
	vars := mux.Vars(r)
	tmp, _ := strconv.Atoi(vars["user_id"])
	userId := int32(tmp)

	if _, ok := s.store[userId]; !ok {
		w.Write([]byte("No such user"))
		return
	}

	tmp, _ = strconv.Atoi(vars["tag_id"])
	tagId := int32(tmp)

	if _, ok := s.store[userId].Tags[tagId]; !ok {
		w.Write([]byte("No such tag"))
	}

	w.Write([]byte(s.store[userId].Tags[tagId].GetNotes() + "\n"))

}

// AddNote of specific user
func (s *service) AddNote(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	content, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		log.Error("Cannot read the data from request")
		w.Write([]byte(err.Error()))
		return
	}
	var req request
	if err := json.Unmarshal(content, &req); err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		w.Write([]byte(err.Error()))
		log.Error("Cannot parse data from json")
		return
	}

	vars := mux.Vars(r)
	tmp, _ := strconv.Atoi(vars["user_id"])
	userId := int32(tmp)

	if _, ok := s.store[userId]; !ok {
		w.Write([]byte("No such user"))
		return
	}

	tmp, _ = strconv.Atoi(vars["tag_id"])
	tagId := int32(tmp)

	if _, ok := s.store[userId].Tags[tagId]; !ok {
		w.Write([]byte("No such tag"))
	}

	s.store[userId].Tags[tagId].AddNoteTag(req.Note)

	log.Info("New note was created")

	w.Write([]byte("Note was successfully created\n"))
}
