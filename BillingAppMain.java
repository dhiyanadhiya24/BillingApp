package subscriptionApp;
import java.util.*;
	
	
	// ---------- Base Plan Class ----------
	abstract class Plan {
	    protected int planId;
	    protected String name;
	    protected double monthlyPrice;
	    protected List<String> features;
	    protected int trialDays;

	    public Plan(int planId, String name, double monthlyPrice, List<String> features, int trialDays) {
	        this.planId = planId;
	        this.name = name;
	        this.monthlyPrice = monthlyPrice;
	        this.features = features;
	        this.trialDays = trialDays;
	    }

	    public int getPlanId() { return planId; }
	    public String getName() { return name; }
	    public double getMonthlyPrice() { return monthlyPrice; }
	    public List<String> getFeatures() { return features; }
	    public int getTrialDays() { return trialDays; }

	    // Overridable method
	    public abstract double computeAmount();

	    @Override
	    public String toString() {
	        return "Plan: " + name + " | Price: " + monthlyPrice + " | Trial: " + trialDays + " days";
	    }
	}

	// ---------- Subclass MonthlyPlan ----------
	class MonthlyPlan extends Plan {
	    public MonthlyPlan(int planId, String name, double monthlyPrice, List<String> features, int trialDays) {
	        super(planId, name, monthlyPrice, features, trialDays);
	    }

	    @Override
	    public double computeAmount() {
	        return monthlyPrice; // normal monthly billing
	    }
	}

	// ---------- Subclass AnnualPlan ----------
	class AnnualPlan extends Plan {
	    public AnnualPlan(int planId, String name, double monthlyPrice, List<String> features, int trialDays) {
	        super(planId, name, monthlyPrice, features, trialDays);
	    }

	    @Override
	    public double computeAmount() {
	        return monthlyPrice * 12 * 0.9; // 10% discount for annual
	    }
	}

	// ---------- Subscriber Class ----------
	class Subscriber {
	    private int id;
	    private String name;
	    private String email;
	    private Plan currentPlan;
	    private String status; // Active, Suspended, Cancelled

	    public Subscriber(int id, String name, String email, Plan currentPlan) {
	        this.id = id;
	        this.name = name;
	        this.email = email;
	        this.currentPlan = currentPlan;
	        this.status = "Active";
	    }

	    public int getId() { return id; }
	    public String getName() { return name; }
	    public Plan getCurrentPlan() { return currentPlan; }
	    public String getStatus() { return status; }

	    public void subscribe(Plan plan) {
	        this.currentPlan = plan;
	        this.status = "Active";
	    }

	    public void changePlan(Plan newPlan) {
	        this.currentPlan = newPlan;
	        System.out.println(name + " switched to " + newPlan.getName());
	    }

	    public void suspend() { status = "Suspended"; }
	    public void cancel() { status = "Cancelled"; }
	}

	// ---------- Invoice Class ----------
	class Invoice {
	    private int invoiceNo;
	    private int subscriberId;
	    private double amount;
	    private Date dueDate;
	    private String state; // Pending, Paid, Overdue

	    public Invoice(int invoiceNo, int subscriberId, double amount, Date dueDate) {
	        this.invoiceNo = invoiceNo;
	        this.subscriberId = subscriberId;
	        this.amount = amount;
	        this.dueDate = dueDate;
	        this.state = "Pending";
	    }

	    public int getInvoiceNo() { return invoiceNo; }
	    public double getAmount() { return amount; }
	    public String getState() { return state; }
	    public Date getDueDate() { return dueDate; }

	    public void markPaid() { this.state = "Paid"; }
	    public void markOverdue() { this.state = "Overdue"; }

	    @Override
	    public String toString() {
	        return "Invoice#" + invoiceNo + " | Subscriber: " + subscriberId +
	                " | Amount: " + amount + " | Due: " + dueDate + " | State: " + state;
	    }
	}

	// ---------- Billing Service ----------
	class BillingService {
	    private List<Invoice> invoices = new ArrayList<>();
	    private int invoiceCounter = 100;

	    // Overloaded methods
	    public Invoice generateInvoice(Subscriber sub) {
	        return generateInvoice(sub, false, 0.0);
	    }

	    public Invoice generateInvoice(Subscriber sub, boolean prorated, double discount) {
	        double baseAmount = sub.getCurrentPlan().computeAmount();
	        if (prorated) baseAmount *= 0.5; // half-month charge
	        if (discount > 0) baseAmount -= discount;

	        Invoice inv = new Invoice(invoiceCounter++, sub.getId(), baseAmount,
	                new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)); // 7 days due
	        invoices.add(inv);
	        return inv;
	    }

	    public void recordPayment(int invoiceNo) {
	        for (Invoice inv : invoices) {
	            if (inv.getInvoiceNo() == invoiceNo) {
	                inv.markPaid();
	                System.out.println("Invoice " + invoiceNo + " marked as Paid.");
	                return;
	            }
	        }
	        System.out.println("Invoice not found.");
	    }

	    public void checkOverdues() {
	        Date today = new Date();
	        for (Invoice inv : invoices) {
	            if (inv.getState().equals("Pending") && today.after(inv.getDueDate())) {
	                inv.markOverdue();
	            }
	        }
	    }

	    public void revenueReport() {
	        double total = 0;
	        for (Invoice inv : invoices) {
	            if (inv.getState().equals("Paid")) total += inv.getAmount();
	        }
	        System.out.println("Total Revenue Collected: " + total);
	    }

	    public void showInvoices() {
	        for (Invoice inv : invoices) {
	            System.out.println(inv);
	        }
	    }
	}

	// ---------- Main Class ----------
	public class BillingAppMain {
	    public static void main(String[] args) {
	        // Create Plans
	        Plan basicMonthly = new MonthlyPlan(1, "Basic Monthly", 500, Arrays.asList("Feature A", "Feature B"), 7);
	        Plan premiumAnnual = new AnnualPlan(2, "Premium Annual", 1000, Arrays.asList("Feature X", "Feature Y", "Feature Z"), 14);

	        // Create Subscribers
	        Subscriber s1 = new Subscriber(101, "Alice", "alice@mail.com", basicMonthly);
	        Subscriber s2 = new Subscriber(102, "Bob", "bob@mail.com", premiumAnnual);

	        // Billing Service
	        BillingService billing = new BillingService();

	        // Generate Invoices
	        Invoice inv1 = billing.generateInvoice(s1);
	        Invoice inv2 = billing.generateInvoice(s2, true, 100); // prorated with discount

	        // Show Invoices
	        billing.showInvoices();

	        // Record Payment
	        billing.recordPayment(inv1.getInvoiceNo());

	        // Check overdues and revenue report
	        billing.checkOverdues();
	        billing.revenueReport();
	    }
	}


