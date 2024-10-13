**Identity Reconciliation Application**
**Overview**
The Identity Reconciliation Application is designed to manage and update user contacts in a system. This application ensures that user information remains consistent, particularly when changes occur in a user’s primary and secondary contacts. The system can handle cases where a primary contact might become a secondary contact or vice versa, depending on specific conditions.

**Key Features**
Primary to Secondary Contact Handling:

If certain conditions are met, the application can turn a primary contact into a secondary contact, or update their details.
Flexible Contact Updates:

The system can accommodate changes to user contact information while maintaining data integrity, ensuring there is no overlap or duplication.
Identity Resolution:

The system resolves conflicts between multiple identities by determining whether a contact should be marked as primary or secondary based on predefined rules.
**How It Works**
1. Contact Types:
Primary Contact: The main point of contact for a user.
Secondary Contact: Additional contacts associated with the user but not the primary.
2. Conditions for Contact Changes:
The application checks for specific conditions under which a primary contact should be changed to a secondary contact or vice versa. For example:

If a user updates their contact information and another contact already exists as the primary, the existing contact may become secondary to maintain consistency.
3. Identity Reconciliation Process:
When changes are made to a user’s identity or contact information, the system performs a reconciliation process to decide the correct status of each contact (primary or secondary).
The system ensures there are no two primary contacts for a user, preventing conflicts in identity resolution.
Example Scenario:
Let’s say we have a user with the following contacts:

Primary Contact: John (john@example.com)
Secondary Contact: Sarah (sarah@example.com)
Now, if John updates his contact information and the system finds that Sarah should actually be the primary contact based on certain conditions, the system will:

Reconcile the contacts.
Update Sarah to be the new Primary Contact.
Change John’s status to Secondary Contact.
**What I Did in This Project**
Implemented Identity Reconciliation: I successfully implemented the logic to reconcile user contacts, handling complex cases where primary and secondary contacts need to be swapped or updated based on conditions.

Optimized the Code: I made sure that the code runs efficiently and that the system updates contact information in real-time without causing delays or errors.

Tested Different Scenarios: I ran several test cases to ensure that the application handles various scenarios, such as:

When a primary contact should be downgraded to a secondary contact.
When no changes are needed, and the system should keep the existing primary contact.
