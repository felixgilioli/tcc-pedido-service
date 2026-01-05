package br.com.felixgilioli.pedido.entity

data class CPF(
    val value: String
) {
    init {
        require(isValid()) { "CPF inválido: $value" }
    }

    private fun isValid(): Boolean {
        val cpf = value

        if (!cpf.matches(Regex("\\d{11}"))) return false

        val digits = cpf.map { it.digitToInt() }
        if (digits.distinct().size == 1) return false

        fun calculateCheckDigit(digits: List<Int>): Int {
            val sum = digits.mapIndexed { i, digit -> digit * (digits.size + 1 - i) }.sum()
            val remainder = sum % 11
            return if (remainder < 2) 0 else 11 - remainder
        }

        return digits[9] == calculateCheckDigit(digits.take(9)) &&
                digits[10] == calculateCheckDigit(digits.take(10))
    }
}