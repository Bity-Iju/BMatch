Requirements Document
1. Application Overview
App Name: BMate
Platform: Mobile App (iOS & Android)
Target Users: HIV-positive individuals in Nigeria
Language: Simple English
Description: BMate is a matchmaking mobile application designed exclusively for HIV-positive users in Nigeria. It enables users to discover compatible matches, communicate in real time, and build meaningful relationships that may lead to marriage. The app provides a safe, private, and professional environment tailored to the needs of its community.
2. Users and Use Cases
2.1 Target Users
HIV-positive adults in Nigeria seeking romantic connections or companionship
2.2 Core Use Cases
A new user views and accepts the consent/disclaimer screen, then registers, confirms HIV-positive status, and sets up a profile
A user browses and filters potential matches by state, LGA, age range, and sex preference
Two users mutually like each other, receive a match notification, and begin chatting
A user sends text messages, photos, voice notes, and location in a one-on-one chat
A user manages privacy settings to control what profile information is visible to others
3. Page Structure and Feature Description
3.1 Page Hierarchy
BMate
├── Consent / Disclaimer Screen (pre-registration gate)
├── Onboarding & Auth
│   ├── Splash / Welcome Screen
│   ├── Registration (Multi-step)
│   ├── Login
│   └── Forgot Password / OTP Reset
├── Main App (Bottom Tab Navigation)
│   ├── Discover (Matchmaking)
│   ├── Matches
│   ├── Messages (Chat List)
│   ├── Notifications
│   └── Profile
│       └── Settings
3.2 Onboarding and Authentication
Consent / Disclaimer Screen
A full-screen pop-up that appears before the registration page is shown
Displays the app consent and disclaimer text
Two actions: "I Agree" (proceeds to registration) and "Decline" (user cannot proceed to register; remains on or returns to the Welcome Screen)
This screen must be acknowledged before any registration step is accessible
Splash / Welcome Screen
Displays app name and tagline
Entry points: Register and Login
Registration (Multi-step Form)
Step 1 — Personal Details: Full name, Age, Sex, Phone Number, Email, Password, Religion (required; options: Christianity, Muslim, Traditionalist)
Step 2 — Location: Nigerian State, LGA (Local Government Area), Home Address
Step 3 — Health Info: Healthcare Facility name, ART ID / Hospital Number
Step 4 — Avatar Upload: User uploads a profile photo; circular crop applied with pinch-to-zoom support so the user can zoom in or out before confirming the crop
Step 5 — HIV Status Acknowledgment: User explicitly confirms HIV-positive status via a dedicated acknowledgment step
Step 6 — Terms and Conditions: User reads and accepts Terms and Conditions before completing registration
Progress indicator shown across all steps
User cannot proceed to the next step without completing required fields in the current step
Login
Login via Email or Phone Number with Password
Secure session management; session persists until the user explicitly logs out or the session times out due to inactivity
Forgot Password / OTP Reset
User enters registered email or phone number
OTP sent to the provided contact
User enters OTP and sets a new password
3.3 Discover (Matchmaking)
Displays a stack of user profile cards in a swipe-style interface
Each card shows: avatar, first name, age, state, LGA, religion
User can Like or Pass each profile
Filter options: State, LGA, Age Range, Sex Preference
When two users mutually like each other, a mutual match is created and both users receive a match notification
3.4 Matches
Displays a list of all mutual matches
Each match entry shows: avatar, name, state
Tapping a match opens the one-on-one chat with that user
3.5 Messages (Chat)
Chat List
Lists all active conversations sorted by most recent message
Each entry shows: avatar, name, last message preview, timestamp, unread count
One-on-One Chat Screen
Real-time text messaging
Photo/image sharing
Voice note recording and playback
Location sharing
Message status indicators:
Single tick or equivalent: Sent
Double tick (muted/grey color): Delivered but unread
Double tick (distinct color, e.g. blue): Read by recipient
Chat history persisted on device (locally stored)
Device Push Notification for Chat Messages
When a chat message is received, a push notification appears in the device's native notification bar (system-level), showing the sender's name and a preview of the message
This applies regardless of whether the app is in the foreground or background
3.6 Notifications
Push Notifications
Triggered for: new match, new message (including device notification bar as described in 3.5), new like received
In-App Notification Feed
Chronological list of all notification events (matches, messages, likes)
Unread notifications visually distinguished from read ones
3.7 Profile
Own Profile
Displays: avatar, name, age, sex, state, LGA, religion, healthcare facility, bio/about me
Edit profile: update any profile field, re-upload avatar
Privacy settings: user controls which fields are visible to other users (e.g., hide home address, hide ART ID / Hospital Number)
Other Users' Profiles
Displays fields the profile owner has set as visible
Option to Like or Pass directly from profile view
3.8 Settings
Edit Profile (redirects to own profile edit)
Notification Preferences: toggle push notifications on/off per category (matches, messages, likes)
Privacy Settings: manage field visibility
Account Security: change password
Logout
Delete Account: permanently removes account and associated data
3.9 Local Storage and Backup
All user data stored on device: registration credentials, chat history, photos, voice notes, location data, uploads
Automatic backup runs every 7 days; each backup appends to the previous backup without overwriting it
Backup stored in local device storage
4. Business Rules and Logic
4.1 Matching Logic
A match is only created when both users have liked each other (mutual like)
A user cannot chat with another user unless a mutual match exists
Passing a user removes them from the current discovery queue; they may reappear in future sessions
4.2 Registration Rules
The consent/disclaimer screen must be accepted before registration can begin; declining blocks access to registration
ART ID / Hospital Number field is required and must be provided during registration
Religion field is required; user must select one of: Christianity, Muslim, Traditionalist
HIV status acknowledgment step is mandatory; registration cannot be completed without it
Terms and Conditions must be accepted to complete registration
Email and phone number must be unique per account
4.3 Privacy Rules
Home Address and ART ID / Hospital Number are hidden from other users by default
Users can choose to make additional fields visible or hidden via privacy settings
Fields hidden by the user are not transmitted to or displayed on other users' profile views
4.4 Session Management
Session remains active until the user explicitly logs out, deletes the account, or the inactivity timeout is triggered
If the user is inactive (no interaction with the app) for approximately 1 minute while the app is open in the foreground, the session is automatically terminated and the user is redirected to the Login screen
On app relaunch, if a valid session exists and has not timed out, the user is taken directly to the main app
4.5 Backup Rules
Backup is triggered automatically every 7 days from the date of last backup
Each backup appends new data to the existing backup file; previous backup data is not overwritten
Backup is stored locally on the device only
4.6 Chat Rules
Chat history is persisted locally on the device
Voice notes are recorded within the app and stored locally
Location shared in chat reflects the user's current location at the time of sharing
Outgoing messages display status progression: Sent → Delivered (double tick, unread color) → Read (double tick, read color)
4.7 Admin Policy
There is no admin panel, back-office interface, or any administrative management system of any kind. BMate is a user-only mobile application.
5. Exceptions and Edge Cases
Scenario	Handling
User declines the consent/disclaimer screen	User cannot proceed to registration; returned to or remains on the Welcome Screen
User attempts to proceed in registration with missing required fields	Step does not advance; required fields highlighted
OTP entered incorrectly during password reset	Error message shown; user may request a new OTP
User tries to open chat without a mutual match	Chat is not accessible; user directed to Discover
No profiles available in Discover matching current filters	Empty state message displayed; prompt to adjust filters
Avatar upload fails	Error message shown; user prompted to retry
Push notification permission denied by user	In-app notification feed remains functional; push notifications disabled
User deletes account	All local data and server-side account data permanently removed
Backup storage space insufficient on device	Backup skipped; user notified that backup could not be completed
User inactive for approximately 1 minute in foreground	Session automatically terminated; user redirected to Login screen
6. Acceptance Criteria
A new user opens the app, views the consent/disclaimer screen, taps "I Agree", completes all registration steps including the Religion field, HIV status acknowledgment, and Terms and Conditions acceptance, and lands on the Discover screen.
The user browses profile cards (each showing avatar, name, age, state, LGA, and religion), applies a filter by State and Age Range, and likes a profile.
The liked user also likes back; both users receive a mutual match notification.
The user navigates to Matches, taps the matched user, and the chat screen opens.
The user sends a text message, a photo, a voice note, and a location pin in the chat; all messages display correct status indicators (delivered/unread vs. read distinguished by double-tick color).
A chat message is received and a push notification appears in the device's native notification bar showing the sender's name and message preview.
The user navigates to Profile, edits the bio field, updates the avatar using pinch-to-zoom within the circular crop, and saves changes successfully.
The user opens Settings, hides the Home Address field via Privacy Settings, and confirms the field is no longer visible on their public profile.
The user leaves the app idle in the foreground for approximately 1 minute; the session times out and the user is redirected to the Login screen.
The user logs back in using email and password; the previous session data and chat history are intact.
7. Out of Scope (Not Implemented in This Version)
Admin panel or any back-office management interface (explicitly excluded; this is a user-only mobile app)
Video calling or video messaging
Group chats or community forums
In-app purchases, subscriptions, or payment features
User verification beyond ART ID / Hospital Number entry (e.g., document upload verification)
Cloud backup (all backup is local device only)
Web version of the application
Multi-language support (English only)
User blocking or reporting system
