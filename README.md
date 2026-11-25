# Bus Route & Ticketing System

**PS No. 67 | Data Structures & Algorithms (DSA) Project | Language: Java**

A console-based Java application to manage bus routes, stops and ticket bookings. Route stops are stored in a **circular linked list**, waiting passengers in a **queue**, seat occupancy in a **2D array**, and tickets are looked up using **hashing (HashMap)**. All data is loaded at startup and saved on exit using **file handling**.

---

## Student Details

| Field | Details |
|---|---|
| **Name** | Pukka Chandrashekhar |
| **ERP ID** | RU-25-11057 |
| **Program** | B.Tech CSE (AI/ML) |
| **Section** | 3C |
| **Semester** | 3rd Semester |
| **Academic Session** | 2026-27 |
| **Subject** | Data Structures & Algorithms (DSA) |
| **University** | Rungta International Skills University |
| **School** | School of Computer Science and Engineering |

---

## Problem Statement

Manage bus routes, stops and ticket bookings. Represent route stops using a circular linked list, use a queue for passengers waiting for seats, store seat occupancy in a 2D array, and use hashing for ticket lookup.

**Note:** Each bus has 2 rows with 3 seats per row (6 seats total); route traversal wraps to the first stop. Data file: `bus_routes.txt`.

---

## Features

1. **Show route** – displays all stops of a bus and marks where the bus currently is.
2. **Book ticket** – allocates the first free seat and generates a unique ticket ID (`BT101`, `BT102`, ...).
3. **Waiting queue** – if the bus is full, the passenger is added to a waiting queue.
4. **Cancel ticket** – frees the seat and automatically gives it to the next waiting passenger.
5. **Search ticket** – fast ticket lookup by ID using a HashMap.
6. **Next stop** – moves the bus to the next stop; after the last stop it wraps back to the first.
7. **Show seats** – displays the 2×3 seat layout and the waiting list.
8. **Shortest distance** – finds the shortest road distance between two stops using Dijkstra's algorithm (bonus feature).
9. **File handling** – loads routes and tickets at startup, saves everything on exit.

---

## Data Structures Used

| Concept | Where it is used | Implementation |
|---|---|---|
| **Circular Linked List** | Route stops of each bus | `Stop` class; last stop's `next` points back to the head |
| **Queue** | Passengers waiting for a seat | `Queue<String>` using `LinkedList` (FIFO) |
| **2D Array** | Seat occupancy (2 rows × 3 seats) | `String[2][3]` – stores ticket ID, `null` means free |
| **Hashing** | Ticket lookup by ID | `HashMap<String, Ticket>` (average O(1) search) |
| **File Handling** | Persistent data | `bus_routes.txt` via `Scanner` and `PrintWriter` |
| **Graph + Priority Queue** | Shortest distance between stops | Dijkstra's algorithm with `HashMap` adjacency list |

---

## Project Structure

```
Bus-Route-Ticketing-System/
│
├── BusSystem.java        # Complete source code
├── bus_routes.txt        # Data file (auto-created on first run)
└── README.md             # Project documentation
```

---

## How to Compile and Run

**Requirements:** Java JDK 11 or above.

```bash
# 1. Clone the repository
git clone https://github.com/BeyondReason-C/Bus-Route-Ticketing-System.git
cd Bus-Route-Ticketing-System


# 2. Compile
javac BusSystem.java

# 3. Run
java BusSystem
```

On the first run, `bus_routes.txt` is created automatically with two default buses (B101 and B102) and their roads.

---

## Menu

```
1 Show route    2 Book ticket   3 Cancel ticket
4 Search ticket 5 Next stop     6 Show seats
7 Shortest distance             0 Save & exit
```

---

## Data File Format (`bus_routes.txt`)

| Line type | Format | Example |
|---|---|---|
| Bus and stops | `BUS,<busId>,<stop1>,<stop2>,...` | `BUS,B101,Delhi,Jaipur,Agra,Mathura` |
| Road | `ROAD,<stopA>,<stopB>,<km>` | `ROAD,Delhi,Jaipur,280` |
| Ticket | `TICKET,<ticketId>,<name>,<busId>,<seat>` | `TICKET,BT101,Aman,B101,A1` |
| Waiting passenger | `WAIT,<busId>,<name>` | `WAIT,B101,Riya` |

### Default data

| Bus | Route (circular) |
|---|---|
| B101 | Delhi → Jaipur → Agra → Mathura → (back to Delhi) |
| B102 | Mumbai → Pune → Nashik → (back to Mumbai) |

---

## Test Cases

| # | Type | Input | Expected Output |
|---|---|---|---|
| 1 | Normal input | Book a seat on bus B101 for Aman | `Ticket booked: BT101 \| Aman \| Bus B101 \| Seat A1` |
| 2 | Boundary / minimum input | All 6 seats are booked; Riya requests a seat | `Bus full. Riya added to waiting queue.` |
| 3 | Invalid input | Search ticket BT999 | `Ticket BT999 not found.` |
| 4 | Different valid scenario | Move to the next stop after the final stop | `Next stop: Delhi (first stop, route wrapped)` |

> **Note:** Ticket IDs continue from the saved data in `bus_routes.txt`. To get `BT101` again for Test Case 1, delete `bus_routes.txt` and run the program fresh.

### Sample Run (Test Case 1)

```
Choice: 2
Bus id: B101
Passenger name: Aman
Ticket booked: BT101 | Aman | Bus B101 | Seat A1
```

### Sample Run (Test Case 3)

```
Choice: 4
Ticket id: BT999
Ticket BT999 not found.
```

---

## How It Works

- **Booking:** Scans the 2×3 seat array row by row and gives the first free seat. A seat that is already occupied is never booked again.
- **Full bus:** If no seat is free, the passenger name is added to the bus's waiting queue.
- **Cancellation:** The ticket is removed from the HashMap, the seat is freed, and the passenger at the front of the queue (if any) is given that seat immediately.
- **Next stop:** The bus pointer moves to `now.next`. Since the last stop links back to the first, the route wraps around automatically.
- **Save / Load:** On exit, all buses, roads, tickets and waiting passengers are written to `bus_routes.txt`; on startup, they are read back and the seat array, HashMap and queues are rebuilt.

---

## Time Complexity

| Operation | Complexity |
|---|---|
| Search ticket (HashMap) | O(1) average |
| Book ticket (scan 2×3 seats) | O(1) – fixed size of 6 seats |
| Cancel ticket | O(1) |
| Add / remove from waiting queue | O(1) |
| Next stop | O(1) |
| Show route | O(n), n = number of stops |
| Shortest distance (Dijkstra) | O((V + E) log V) |

---

## Input Handling

- Bus IDs and ticket IDs are case-insensitive (`b101` works the same as `B101`).
- Invalid bus ID → `Bus not found.`
- Invalid ticket ID → `Ticket <id> not found.`
- Invalid menu option → `Wrong choice.`
- Commas are removed from passenger names so the data file stays valid.
- Unknown stops in shortest distance → `Stop not found.`

---

## Future Improvements

- Add a GUI or web interface.
- Support more buses and custom seat layouts.
- Add fares and payment details to tickets.
- Add a menu option to add new buses and routes at runtime.

---

## Concepts Covered

`Circular Linked List` · `Queue` · `2D Array` · `Hashing (HashMap)` · `File Handling` · `Graph (Dijkstra)`

---

## Author

**Pukka Chandrashekhar**

ERP ID: RU-25-11057|
B.Tech CSE (AI/ML) | Section 3C | 3rd Semester | 2026-27

Rungta International Skills University
