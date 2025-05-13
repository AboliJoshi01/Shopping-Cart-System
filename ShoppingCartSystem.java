import java.sql.*;
import java.util.Scanner;
public class ShoppingCartSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nShopping Cart System:");
            System.out.println("1. Add Item to Cart");
            System.out.println("2. View Cart");
            System.out.println("3. Update Item Quantity");
            System.out.println("4. Remove Item from Cart");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    addItemToCart(scanner);
                    break;
                case 2:
                    viewCart();
                    break;
                case 3:
                    updateItemQuantity(scanner);
                    break;
                case 4:
                    removeItemFromCart(scanner);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        }
    }
    private static void addItemToCart(Scanner scanner) {
        try (Connection conn = ShoppingCart.getConnection()) {
            System.out.print("Enter item ID to add: ");
            int itemId = scanner.nextInt();
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            String checkItem = "SELECT quantity_in_stock FROM items WHERE item_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkItem);
            checkStmt.setInt(1, itemId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int availableStock = rs.getInt("quantity_in_stock");
                if (availableStock < quantity) {
                    System.out.println("Not enough stock available.");
                    return;
                }
                String query = "INSERT INTO cart (item_id, quantity) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, itemId);
                stmt.setInt(2, quantity);
                stmt.executeUpdate();
                System.out.println("Item added to cart.");
                String updateStock = "UPDATE items SET quantity_in_stock = quantity_in_stock - ? WHERE item_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateStock);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, itemId);
                updateStmt.executeUpdate();
            } else {
                System.out.println("Item not found.");
            }
        } catch (SQLException e) {
            System.out.println("not connected");
        }
    }
    private static void viewCart() {
        try (Connection conn = ShoppingCart.getConnection()) {
            String query = "SELECT c.cart_id, i.name, i.price, c.quantity FROM cart c JOIN items i ON c.item_id = i.item_id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Items in your cart:");
            while (rs.next()) {
                int cartId = rs.getInt("cart_id");
                String itemName = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                double totalPrice = price * quantity;
                System.out.printf("Cart ID: %d, Item: %s, Price: %.2f, Quantity: %d, Total: %.2f%n", cartId, itemName, price, quantity, totalPrice);
            }
        } catch (SQLException e) {
            System.out.println("not connected");
        }
    }
    private static void updateItemQuantity(Scanner scanner) {
        try (Connection conn = ShoppingCart.getConnection()) {
            System.out.print("Enter cart ID to update: ");
            int cartId = scanner.nextInt();
            System.out.print("Enter new quantity: ");
            int newQuantity = scanner.nextInt();
            String query = "UPDATE cart SET quantity = ? WHERE cart_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, cartId);
            stmt.executeUpdate();
            System.out.println("Cart updated.");
        } catch (SQLException e) {
            System.out.println("not connected");
        }
    }
    private static void removeItemFromCart(Scanner scanner) {
        try (Connection conn = ShoppingCart.getConnection()) {
            System.out.print("Enter cart ID to remove: ");
            int cartId = scanner.nextInt();
            String query = "DELETE FROM cart WHERE cart_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
            System.out.println("Item removed from cart.");
        } catch (SQLException e) {
            System.out.println("not connected");
        }
    }
}
