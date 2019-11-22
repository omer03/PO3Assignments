/**
 * Supermarket Customer check-out and Cashier simulation
 * @author  hbo-ict@hva.nl
 */

import utils.XMLParser;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.*;

public class Product implements Comparable<Product> {
    private String code;            // a unique product code; identical codes designate identical products
    private String description;     // the product description, useful for reporting
    private double price;           // the product's price

    public Product(String code, String description, double price) {
        this.code = code;
        this.description = description;
        this.price = price;
    }

    // TODO implement relevant overrides and/or local classes to be able to
    //  print Products and/or use them in sets, maps and/or priority queues. (Complete)

    /**
     * This overridden method produces a hashcode based on the code of the product.
     *
     * @return The hashcode of the product.
     */
    @Override
    public int hashCode() {
        // Product objects are hashed with their code. So similar codes produce same hash.
        return Objects.hash(code);
    }

    /**
     *  This overridden method checks if two products are equal. Two products are equal if they have the same code.
     *
     * @param obj The object to be compared.
     * @return Whether the two objects are the same.
     */
    @Override
    public boolean equals(Object obj) {
        // Standard code for overriding equals method.
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        return code.equals(other.code); // We only check if code is equal to see if products are the same.
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return description;
    }

    /**
     * read a series of products from the xml stream
     * and add them to the provided products list
     * @param xmlParser
     * @param products
     * @return
     * @throws XMLStreamException
     */
    public static Set<Product> importProductsFromXML(XMLParser xmlParser, Set<Product> products) throws XMLStreamException {
        if (xmlParser.nextBeginTag("products")) {
            xmlParser.nextTag();
            if (products != null) {
                Product product;
                while ((product = importFromXML(xmlParser)) != null) {
                    products.add(product);
                }
            }

            xmlParser.findAndAcceptEndTag("products");
            return products;
        }
        return null;
    }

    /**
     * read a single product from the xml stream
     * @param xmlParser
     * @return
     * @throws XMLStreamException
     */
    public static Product importFromXML(XMLParser xmlParser) throws XMLStreamException {
        if (xmlParser.nextBeginTag("product")) {
            String code = xmlParser.getAttributeValue(null, "code");
            String description = xmlParser.getAttributeValue(null, "description");
            double price = xmlParser.getDoubleAttributeValue(null, "price", 0);

            Product product = new Product(code, description, price);

            xmlParser.findAndAcceptEndTag("product");
            return product;
        }
        return null;
    }

    /**
     * write a single product to the xml stream
     * @param xmlWriter
     * @throws XMLStreamException
     */
    public void exportToXML(XMLStreamWriter xmlWriter) throws XMLStreamException {
        xmlWriter.writeStartElement("product");
        xmlWriter.writeAttribute("code", this.code);
        xmlWriter.writeAttribute("description", this.description);
        xmlWriter.writeAttribute("price", String.format(Locale.US, "%.2f", this.price));
        xmlWriter.writeEndElement();
    }

    /**
     * This method compares the current Product with the next Product.
     * It compares products based on 1 attribute: code
     *
     * @param p The product to be compared
     * @return A negative integer, zero or a positive integer as this product
     * is less than, equal to, or greater than the supplied product object.
     */
    @Override
    public int compareTo(Product p) {
        return this.code.compareTo(p.code);
    }
}
