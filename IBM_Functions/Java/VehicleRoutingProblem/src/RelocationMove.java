public class RelocationMove {

    int fromRoute; // From which route we remove the customer
    int toRoute; // To which route we insert the customer
    int positionOfRelocated; // In which position (of the initial route) we find the customer we want to relocate
    int positionToBeInserted; // In which position (of the new route) we want to insert the customer
    double fromMoveCost; // Move Cost for the route from which we remove a customer
    double toMoveCost; // Move Cost for the route to which we add a customer
    double moveCost; // Total move cost = fromMoveCost + toMoveCost
    int fromUpdLoad; // Updated load for the route from which we remove a customer
    int toUpdLoad; // Updated load for the route to which we insert a customer

    RelocationMove() { }
}
