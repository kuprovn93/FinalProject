import firebase from "firebase";

const firebaseConfig = {
  apiKey: "AIzaSyAovootqoYkPU3dgANlgTNdR4lO7kkPUMk",
  authDomain: "socialboard-b1a1e.firebaseapp.com",
  projectId: "socialboard-b1a1e",
  storageBucket: "socialboard-b1a1e.appspot.com",
  messagingSenderId: "1039428626533",
  appId: "1:1039428626533:web:a511a33d0f102fafaf7e8c",
  measurementId: "G-9Z8FDFRYNP"
};
  firebase.initializeApp(firebaseConfig);
  firebase.analytics();
export const auth = firebase.auth;
const db = firebase.firestore();

export { db };