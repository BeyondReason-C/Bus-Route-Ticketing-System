import java.io.*;
import java.util.*;

public class BusSystem {

    // one stop on a route, the last stop points back to the first (circular)
    static class Stop {
        String name;
        Stop next;
        Stop(String name) { this.name = name; }
    }

    static class Bus {
        String id;
        Stop head, tail, now;
        String[][] seats = new String[2][3];          // ticket id inside, null = free
        Queue<String> wait = new LinkedList<>();      // waiting passengers

        Bus(String id) { this.id = id; }

        void addStop(String name) {
            Stop s = new Stop(name);
            if (head == null) head = tail = now = s;
            else { tail.next = s; tail = s; }
            tail.next = head;
        }
    }

    static class Ticket {
        String id, name, bus, seat;
        Ticket(String id, String name, String bus, String seat) {
            this.id = id; this.name = name; this.bus = bus; this.seat = seat;
        }
        int row() { return seat.charAt(0) - 'A'; }
        int col() { return seat.charAt(1) - '1'; }
    }

    static Map<String, Bus> buses = new LinkedHashMap<>();
    static Map<String, Ticket> tickets = new HashMap<>();           // hashing for ticket lookup
    static Map<String, Map<String, Integer>> roads = new HashMap<>();
    static int count = 101;
    static Scanner sc = new Scanner(System.in);
    static final String FILE = "bus_routes.txt";

    static final String[] DEFAULT = {
        "BUS,B101,Delhi,Jaipur,Agra,Mathura",
        "BUS,B102,Mumbai,Pune,Nashik",
        "ROAD,Delhi,Jaipur,280", "ROAD,Jaipur,Agra,240", "ROAD,Agra,Mathura,58",
        "ROAD,Delhi,Agra,230", "ROAD,Delhi,Mathura,145",
        "ROAD,Mumbai,Pune,150", "ROAD,Pune,Nashik,210", "ROAD,Mumbai,Nashik,165"
    };

    static String ask(String q) {
        System.out.print(q);
        return sc.nextLine().trim();
    }

    static void road(String a, String b, int d) {
        roads.computeIfAbsent(a, k -> new HashMap<>()).put(b, d);
        roads.computeIfAbsent(b, k -> new HashMap<>()).put(a, d);
    }

    static void load() {
        File f = new File(FILE);
        try {
            if (!f.exists()) {
                try (PrintWriter pw = new PrintWriter(f)) {
                    for (String s : DEFAULT) pw.println(s);
                }
            }
            try (Scanner in = new Scanner(f)) {
                while (in.hasNextLine()) {
                    String[] p = in.nextLine().trim().split(",");
                    if (p.length < 3) continue;
                    if (p[0].equals("BUS")) {
                        Bus b = new Bus(p[1]);
                        for (int i = 2; i < p.length; i++) b.addStop(p[i]);
                        buses.put(b.id, b);
                    } else if (p[0].equals("ROAD")) {
                        road(p[1], p[2], Integer.parseInt(p[3]));
                    } else if (p[0].equals("TICKET")) {
                        Ticket t = new Ticket(p[1], p[2], p[3], p[4]);
                        tickets.put(t.id, t);
                        buses.get(t.bus).seats[t.row()][t.col()] = t.id;
                        count = Math.max(count, Integer.parseInt(t.id.substring(2)) + 1);
                    } else if (p[0].equals("WAIT")) {
                        buses.get(p[1]).wait.add(p[2]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Problem reading " + FILE + ": " + e.getMessage());
        }
    }

    static void save() {
        try (PrintWriter pw = new PrintWriter(FILE)) {
            for (Bus b : buses.values()) {
                String line = "BUS," + b.id;
                Stop s = b.head;
                if (s != null) do { line += "," + s.name; s = s.next; } while (s != b.head);
                pw.println(line);
            }
            for (String a : roads.keySet())
                for (Map.Entry<String, Integer> e : roads.get(a).entrySet())
                    if (a.compareTo(e.getKey()) < 0)    // write each road only once
                        pw.println("ROAD," + a + "," + e.getKey() + "," + e.getValue());
            for (Ticket t : tickets.values())
                pw.println("TICKET," + t.id + "," + t.name + "," + t.bus + "," + t.seat);
            for (Bus b : buses.values())
                for (String w : b.wait) pw.println("WAIT," + b.id + "," + w);
            System.out.println("Data saved to " + FILE);
        } catch (IOException e) {
            System.out.println("Could not save: " + e.getMessage());
        }
    }

    static void showRoute(Bus b) {
        if (b.head == null) { System.out.println("No stops on this bus."); return; }
        String line = "";
        Stop s = b.head;
        do {
            line += s.name + (s == b.now ? " (bus here)" : "") + " -> ";
            s = s.next;
        } while (s != b.head);
        System.out.println(line + b.head.name + " (back to start)");
    }

    static void nextStop(Bus b) {
        if (b.now == null) { System.out.println("No stops on this bus."); return; }
        b.now = b.now.next;
        System.out.println("Next stop: " + b.now.name + (b.now == b.head ? " (first stop, route wrapped)" : ""));
    }

    static void showSeats(Bus b) {
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                String t = b.seats[r][c];
                System.out.print((char) ('A' + r) + "" + (c + 1) + ":" + (t == null ? "free" : tickets.get(t).name) + "   ");
            }
            System.out.println();
        }
        System.out.println("Waiting: " + b.wait);
    }

    // gives the first free seat, returns false if bus is full
    static boolean seat(Bus b, String name) {
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 3; c++) {
                if (b.seats[r][c] == null) {
                    Ticket t = new Ticket("BT" + count++, name, b.id, (char) ('A' + r) + "" + (c + 1));
                    b.seats[r][c] = t.id;
                    tickets.put(t.id, t);
                    System.out.println("Ticket booked: " + t.id + " | " + name + " | Bus " + b.id + " | Seat " + t.seat);
                    return true;
                }
            }
        }
        return false;
    }

    static void book(Bus b, String name) {
        if (!seat(b, name)) {
            b.wait.add(name);
            System.out.println("Bus full. " + name + " added to waiting queue.");
        }
    }

    static void cancel(String id) {
        Ticket t = tickets.remove(id);
        if (t == null) { System.out.println("Ticket " + id + " not found."); return; }
        Bus b = buses.get(t.bus);
        b.seats[t.row()][t.col()] = null;
        System.out.println("Ticket " + id + " cancelled.");
        if (!b.wait.isEmpty()) {
            String next = b.wait.poll();
            System.out.println(next + " was waiting, giving the seat now.");
            seat(b, next);
        }
    }

    static void search(String id) {
        Ticket t = tickets.get(id);
        if (t == null) System.out.println("Ticket " + id + " not found.");
        else System.out.println(t.id + " | " + t.name + " | Bus " + t.bus + " | Seat " + t.seat);
    }

    static String find(String s) {
        for (String k : roads.keySet()) if (k.equalsIgnoreCase(s)) return k;
        return null;
    }

    // dijkstra on the road map between stops
    static void shortest(String from, String to) {
        from = find(from);
        to = find(to);
        if (from == null || to == null) { System.out.println("Stop not found."); return; }

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());
        dist.put(from, 0);
        pq.add(Map.entry(from, 0));

        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> cur = pq.poll();
            String u = cur.getKey();
            if (cur.getValue() > dist.get(u)) continue;      // old entry, skip
            for (Map.Entry<String, Integer> e : roads.get(u).entrySet()) {
                int nd = dist.get(u) + e.getValue();
                if (nd < dist.getOrDefault(e.getKey(), Integer.MAX_VALUE)) {
                    dist.put(e.getKey(), nd);
                    prev.put(e.getKey(), u);
                    pq.add(Map.entry(e.getKey(), nd));
                }
            }
        }

        if (!dist.containsKey(to)) { System.out.println("No road between " + from + " and " + to); return; }
        LinkedList<String> path = new LinkedList<>();
        for (String s = to; s != null; s = prev.get(s)) path.addFirst(s);
        System.out.println(String.join(" -> ", path) + " (" + dist.get(to) + " km)");
    }

    public static void main(String[] args) {
        load();
        while (true) {
            System.out.println("\n1 Show route    2 Book ticket   3 Cancel ticket");
            System.out.println("4 Search ticket 5 Next stop     6 Show seats");
            System.out.println("7 Shortest distance             0 Save & exit");
            String ch = ask("Choice: ");
            if (ch.equals("0")) break;

            Bus b = null;
            if (ch.matches("[1256]")) {
                b = buses.get(ask("Bus id: ").toUpperCase());
                if (b == null) { System.out.println("Bus not found."); continue; }
            }

            switch (ch) {
                case "1": showRoute(b); break;
                case "2": book(b, ask("Passenger name: ").replace(",", "")); break;
                case "3": cancel(ask("Ticket id: ").toUpperCase()); break;
                case "4": search(ask("Ticket id: ").toUpperCase()); break;
                case "5": nextStop(b); break;
                case "6": showSeats(b); break;
                case "7": shortest(ask("From stop: "), ask("To stop: ")); break;
                default: System.out.println("Wrong choice.");
            }
        }
        save();
    }
}
