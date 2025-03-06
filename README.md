## 🎬 MovieApp  

MovieApp is a modern Android application that allows users to explore movies, view details, save favorites, and manage their profiles. The app integrates with [TMDB API](https://www.themoviedb.org/) for fetching movie data and uses Firebase/Firestore for authentication and data storage.  

### 🔐 Authentication  
- User Registration (Name, Mobile, City required)  
- Login with "Remember Me" (Session saved if checked)  
- Firebase Authentication  

### 🎥 Home Page  
- **Movie Categories:** Popular, Now Playing, Top Rated, Upcoming  
- Uses **ViewPager2** + **TabLayout** for seamless navigation  
- **Search Bar** (Min 2 characters required) for searching movies  
- **Smooth Scrolling** with CoordinatorLayout  

### 📝 Movie Details  
- **Movie Information:** IMDb Rating, Vote Count, Genre, Duration, Release Year  
- **Tabs (ViewPager2 + TabLayout):**  
  - 🎭 **Cast** (Clicking on actor/actress opens BottomSheet with details)  
  - 📖 **About Movie**  
- **Bookmark Movies** (Save to Watchlist)  

### 📌 Watchlist  
- List of saved movies  
- **Remove individual movies** (Long press)  
- **Clear all movies** (Trash bin icon)  

### 👤 Profile Page  
- View & update profile details  
- Change app language  
- Log out
- Uses **RecyclerView** for UI  

---

## 🛠️ Technologies Used  
- **Kotlin** (Primary language)  
- **MVVM** Design Pattern  
- **Clean Architecture** (Presentation & Data layers)  
- **StateFlow** (Instead of LiveData)  
- **Hilt Dagger** (Dependency Injection)  
- **Paging Library** (For better performance)  
- **Firebase & Firestore** (Authentication & Data storage)  
- **Preference Datastore** (Remember me)  
- **Retrofit** (API calls)  
- **Glide** (Image loading)  
- **Snackbar** (Error messages and notifications)  
- **BottomNavigationView** (For Home & Watchlist navigation)  
- **ViewPager2 & TabLayout** (Navigation between sections)  
- **RecyclerView** (Efficient list rendering)  
- **CoordinatorLayout** (Better scrolling experience)  
- **Safe Args & Navigation** (With `popUpToInclusive` and `popUpTo`)  





## 🔗 API Reference  
- [TMDB API Docs](https://developers.themoviedb.org/)  

---

## 📌 Future Enhancements  
- Add user reviews & ratings  
- Implement dark mode  
- Offline movie saving  

---

## 💡 Contribution  
Feel free to fork the repository and submit pull requests!  

---

Let me know if you need any further edits! 🚀