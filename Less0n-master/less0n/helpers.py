GPA_TABLE = {
    'A+': 4.33,
    'A':  4.00,
    'A-': 3.67,
    'B+': 3.33,
    'B':  3.00,
    'B-': 2.67,
    'C+': 2.33,
    'C':  2.00,
    'C-': 1.67,
    'D':  1.00,
    'F':  0.00,
}


def letter_grade_to_numeric(letter: str) -> float:
    """Convert a letter grade to a numerical grade.

    Args:
        letter: A letter grade.

    Returns:
        A numerical grade.

    """
    return GPA_TABLE.get(letter, 0.00)
