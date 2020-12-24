// Written by Thomas Bastis
// This code calculates the edit distance between two strings, and print out the edit distance,
// along with an optimal alignment of the two strings.
// "edit distance" is the smallest number of single-letter insertions, deletions, or
// substitutions needed to change one string to the other.

import java.util.Scanner;

class Main {

    /** Takes as input to stdin a pair of strings, and prints to stdout the edit distance of the two
     * strings, as well as a representation of their optimal alignment. <br>
     * Precondition: The first string is provided on the first line of input, and the second string
     * on the second line of input. */
    public static void main(String args[]) throws java.io.IOException {
        Scanner scan= new Scanner(System.in);
        String s1= scan.nextLine(); // Get the first input string
        String s2= scan.nextLine(); // Get the second input string

        EditDistance sol= getEditDistance(s1, s2); // Execute algorithm

        scan.close();
        System.out.println(sol.distance); // Print edit distance
        System.out.println(sol.s1); // print "aligned" version of s1
        System.out.println(sol.s2); // print "aligned" version of s2
    }

    /** Returns the EditDistance representation of the edit distance between strings s1 and s2. */
    private static EditDistance getEditDistance(String s1, String s2) {

        char[] s1arr= s1.toCharArray();
        char[] s2arr= s2.toCharArray();

        int m= s1arr.length;
        int n= s2arr.length;

        int[][] A= new int[m + 1][n + 1]; // A[i][j] = the edit distance between the first i
                                          // characters of s1 and the first j characters of s2.

        for (int i= 0; i <= m; i++ ) { // Fill the first column of A with correct values
            A[i][0]= i;
        }

        for (int j= 1; j <= n; j++ ) { // Fill the first row of A with correct values
            A[0][j]= j;
        }

        for (int i= 0; i < m; i++ ) {
            for (int j= 0; j < n; j++ ) { // We wish to calculate A[i+1][j+1]
                if (s1arr[i] == s2arr[j]) { // if character i in s1 is equal to character j in s2
                    A[i + 1][j + 1]= A[i][j]; // Those characters contribute no edit distance if
                                              // aligned, so A[i+1][j+1] has the same edit distance
                                              // as A[i][j].
                } else {
                    A[i + 1][j + 1]= Math.min(A[i][j], Math.min(A[i][j + 1], A[i + 1][j])) + 1;
                    // A[i+1][j+1] (edit distance up through chars at i+1 and j+1) = minimum of:
                    // A[i][j] (edit distance up through chars at i and j)
                    // A[i][j + 1] (edit distance up through chars at i and j+1), and
                    // A[i + 1][j] (edit distance up through chars at i+1 and j)
                    // plus 1, to account for edit distance of characters we're processing.
                }
            }
        }

        StringBuilder sb1= new StringBuilder();
        StringBuilder sb2= new StringBuilder();

        while (m > 0 || n > 0) { // We haven't added every letter of s1 to sb1, and every letter of
                                 // s2 to sb2.

            if (m == 0) { // We've added every letter of s1 to sb1
                sb1.append(' ');
                sb2.append(s2arr[n - 1]); // Add the next remaining unadded letter of s2 to sb2.
                n-- ; // Decrement number of letters remaining of s2 to be added.
                continue;
            }

            if (n == 0) { // We've added every letter of s2 to sb2
                sb1.append(s1arr[m - 1]); // Add the next remaining unadded letter of s1 to sb1.
                sb2.append(' ');
                m-- ; // Decrement number of letters remaining of s1 to be added.
                continue;
            }

            int up= A[m - 1][n]; // edit distance in cell above
            int left= A[m][n - 1]; // edit distance in cell to the left
            int diag= A[m - 1][n - 1]; // edit distance in diagonal cell

            int min= Math.min(diag, Math.min(left, up)); // min of those 3 cells

            // The cell which has minimum value must have been part of the path of cells used to
            // calculate the value of A[m][n]. Because we're reconstructing the path we took to get
            // this value backwards, this minimum cell is the one of interest to us.

            if (min == diag) { // The path came from the diagonal cell, indicating it was optimal to
                               // align the two characters.
                sb1.append(s1arr[m - 1]); // Add the next remaining unadded letter of s1 to sb1.
                sb2.append(s2arr[n - 1]); // Add the next remaining unadded letter of s2 to sb2.
                m-- ; // Decrement number of letters remaining of s1 to be added.
                n-- ; // Decrement number of letters remaining of s2 to be added.

            } else if (min == left) { // The path came from the cell to the left, indicating it was
                                      // optimal to align the next character in s2 with a space.
                sb1.append(' ');
                sb2.append(s2arr[n - 1]); // Add the next remaining unadded letter of s2 to sb2.
                n-- ; // Decrement number of letters remaining of s2 to be added.

            } else {// The path came from the cell above, indicating it was optimal to
                // align the next character in s1 with a space.
                sb1.append(s1arr[m - 1]); // Add the next remaining unadded letter of s1 to sb1.
                sb2.append(' ');
                m-- ; // Decrement number of letters remaining of s1 to be added.
            }

        }
        // For efficiency we appended the characters thorughout, meaning that the strings are in
        // reverse. So now we just reverse both of them to get the correct order.
        sb1.reverse();
        sb2.reverse();

        // Return an object holding all of the information we care about.
        return new EditDistance(A[s1arr.length][s2arr.length], sb1.toString(), sb2.toString());
    }

    /** And instance containing information about the edit distance of two strings. */
    public static class EditDistance {
        /** = the edit distance of strings s1 and s2 */
        int distance;
        /** The "aligned" version of s1, with spaces inserted to indicate where adding spaces was
         * optimal. */
        String s1;
        /** The "aligned" version of s1, with spaces inserted to indicate where adding spaces was
         * optimal. */
        String s2;

        /** Returns an instance containing information about the edit distance of two strings. <br>
         * Precondition: distance is the edit distance of strings s1 and s2, and s1 and s2 are the
         * optimally "aligned" versions of s1 and s2 respectively. */
        protected EditDistance(int distance, String s1, String s2) {
            this.distance= distance;
            this.s1= s1;
            this.s2= s2;
        }

    }

}
