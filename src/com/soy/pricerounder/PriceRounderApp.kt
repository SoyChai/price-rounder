package com.soy.pricerounder

import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.math.BigDecimal
import java.math.MathContext

/**
 * JavaFX application for handling the UI.
 */
class PriceRounderApp : Application() {

	private val defaultControlWidth = 350.0
	private val defaultHeader = "Enter a price"
	private val defaultFont = Font.font(24.0)
	private val defaultTextFill = Color.rgb(0, 0, 0, 0.90)

	override fun start(stage: Stage) {
		val header = Label(defaultHeader).apply {
			maxWidth = defaultControlWidth
			font = defaultFont
			textFill = defaultTextFill
		}

		val field = DecimalTextField().apply {
			style = "-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);" // Always show prompt text
			maxWidth = defaultControlWidth
			font = defaultFont
			promptText = "eg: 123.99"
			textProperty().addListener { _, _, inputString ->
				text = inputString
				showRoundedPriceOn(header, inputString)
			}
		}

		val button = Button("Exit").apply {
			maxWidth = defaultControlWidth
			font = defaultFont
			textFill = defaultTextFill
			setOnAction { Platform.exit() }
		}

		val footer = Label().apply {
			maxWidth = defaultControlWidth
			text = "Round any price to the 3 most significant digits.\n\n" +
					"You can drag this window too!"
			textFill = defaultTextFill
			isWrapText = true
		}

		val grid = GridPane().apply {
			style = "-fx-background-color: #CCDDFF; -fx-background-radius: 5" // Blue background with round corners
			alignment = Pos.CENTER
			hgap = 10.0
			vgap = 10.0

			addRow(0, header)
			addRow(1, field, button)
			addRow(2, footer)
		}

		val scene = Scene(grid, 500.0, 300.0).apply {
			fill = Color.TRANSPARENT

			// Whole-window drag support
			var dragOffsetX = 0.0
			var dragOffsetY = 0.0
			setOnMousePressed {
				dragOffsetX = stage.x - it.screenX
				dragOffsetY = stage.y - it.screenY
			}
			setOnMouseDragged {
				stage.x = it.screenX + dragOffsetX
				stage.y = it.screenY + dragOffsetY
			}
		}

		stage.apply {
			title = "Price Rounder"
			this.scene = scene
			initStyle(StageStyle.TRANSPARENT)
			show()
		}
	}

	private fun showRoundedPriceOn(label: Label, priceStr: String?) {
		if (priceStr.isNullOrBlank()) {
			label.text = defaultHeader
			label.textFill = defaultTextFill
			return
		} else try {
			val price = BigDecimal(priceStr)
			val roundedPrice = price.round(MathContext(3)).setScale(price.scale())
			label.text = roundedPrice.toString()
			label.textFill = Color.GREEN
		} catch (e: NumberFormatException) {
			label.text = "Invalid price"
			label.textFill = Color.RED
		}
	}
}

/**
 * Text field which only accepts numeric digits and a single decimal point.
 */
class DecimalTextField : TextField() {

	override fun replaceText(start: Int, end: Int, insertedText: String) {
		if (insertedText.all { it.isDigit() || it == '.' } && text.plus(insertedText).count { it == '.' } <= 1)
			super.replaceText(start, end, insertedText)
	}
}

fun main(args: Array<String>) = Application.launch(PriceRounderApp::class.java)
