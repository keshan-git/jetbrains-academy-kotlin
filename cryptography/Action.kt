package cryptography

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Action {
    fun hide() {
        println("Input image file:")
        val inputImageFileName = readln()

        println("Output image file:")
        val outputImageFileName = readln()

        println("Message to hide:")
        val message = readln().encodeToByteArray()

        println("Password:")
        val password = readln().encodeToByteArray()

        val encMessage = encrypt(message, password)

        try {
            val inputImageFile = File(inputImageFileName)
            val inputImage: BufferedImage = ImageIO.read(inputImageFile)

            if (encMessage.size * 8 > inputImage.width * inputImage.height ) {
                println("The input image is not large enough to hold this message.")
                return
            }

            for (y in 0 until inputImage.height) {
                for (x in 0 until inputImage.width) {
                    val color = Color(inputImage.getRGB(x, y))
                    val r = color.red
                    val g = color.green
                    var b = color.blue

                    val i = y * inputImage.height + x
                    if ( i < encMessage.size * 8) {
                        val bit = getBit(encMessage[i/ 8].toInt(), 7 -(i % 8))
                        b = setLSB(b, bit)
                        // println("y=$y x=$x i=$i byte=${i / 8} bit=${7 -(i % 8)} lsb=${bit} color_old=${color.blue} color_new=$b")
                    }

                    val colorNew = Color(r, g, b)
                    inputImage.setRGB(x, y, colorNew.rgb)
                }
            }

            val outputFile = File(outputImageFileName)
            ImageIO.write(inputImage, "png", outputFile)
        } catch (ex: Exception) {
            println("Can't read input file!")
            ex.printStackTrace()
            return
        }

//        println("Input Image: $inputImageFileName")
//        println("Output Image: $outputImageFileName")
//        println("Image $outputImageFileName is saved.")

        println("Message saved in $outputImageFileName image.")
    }

    fun show() {
        println("Input image file:")
        val inputImageFileName = readln()

        println("Password:")
        val password = readln().encodeToByteArray()

        try {
            val inputImageFile = File(inputImageFileName)
            val inputImage: BufferedImage = ImageIO.read(inputImageFile)

            var messageBytes = ""
            var message = ByteArray(0)

            outer@ for (y in 0 until inputImage.height) {
                for (x in 0 until inputImage.width) {
                    val color = Color(inputImage.getRGB(x, 0))
                    val b = color.blue

                    // println("color=$b bit=${b%2}")
                    if (messageBytes.length == 8) {
                        message += messageBytes.toByte(2)
                        messageBytes = ""
                    }

                    if (message.size > 3 &&
                            message[message.size - 1].toInt() == 3 &&
                            message[message.size - 2].toInt() == 0 &&
                            message[message.size - 3].toInt() == 0
                            ) {
                        break@outer
                    }

                    messageBytes += b % 2
                }
            }

            val decryptedMessage = decrypt(message, password)
            val result = decryptedMessage.toString(Charsets.UTF_8)

            println("Message:")
            println(result)
        } catch (ex: Exception) {
            println("Can't read input file!")
            ex.printStackTrace()
            return
        }
    }

    private fun setLSB(value: Int, lsb: Int): Int {
        return value and 0xFE or lsb
    }

    private fun getBit(value: Int, position: Int): Int {
        return (value shr position) and 1;
    }

    private fun encrypt(message: ByteArray, password: ByteArray): ByteArray {
        var result = ByteArray(0)

        for (i in message.indices) {
            result += (message[i].toInt() xor password[i % password.size].toInt()).toByte()
        }

        result += 0
        result += 0
        result += 3
        return result
    }

    private fun decrypt(encMessage: ByteArray, password: ByteArray): ByteArray {
        var result = ByteArray(0)

        for (i in 0 until encMessage.size - 3) {
            result += (encMessage[i].toInt() xor password[i % password.size].toInt()).toByte()
        }

        return result
    }

}