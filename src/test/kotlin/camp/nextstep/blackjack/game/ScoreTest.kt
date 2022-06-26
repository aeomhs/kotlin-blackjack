package camp.nextstep.blackjack.game

import camp.nextstep.blackjack.card.Card
import camp.nextstep.blackjack.card.CardNumber
import camp.nextstep.blackjack.card.CardSuit
import camp.nextstep.blackjack.card.DrawnCard
import camp.nextstep.blackjack.card.Hand
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ScoreTest {

    @DisplayName("21점이면 블랙잭이다.")
    @Test
    fun isBlackJackWhenScoreIs21() {
        val score = Score(21)
        assertThat(score.isBlackJack()).isTrue
    }

    @DisplayName("21점이 넘으면 버스트이다.")
    @Test
    fun isBustWhenScoreOver21() {
        val score = Score(22)
        assertThat(score.isBust()).isTrue
    }

    @DisplayName("Ace 는 다른 카드의 점수 합이 10점 이하인 경우 11점으로 계산한다.")
    @Test
    fun aceScoreAs11() {
        val hand = Hand()
        hand.add(DrawnCard(Card(CardSuit.SPADE, CardNumber.FIVE)))
        hand.add(DrawnCard(Card(CardSuit.HEART, CardNumber.FIVE))) // 10
        hand.add(DrawnCard(Card(CardSuit.SPADE, CardNumber.ACE))) // 11

        assertThat(Score.of(hand).value).isEqualTo(21)
    }

    @DisplayName("Ace 는 다른 카드의 점수 합이 10점이 넘으면 1점으로 계산한다.")
    @Test
    fun aceScoreAs1() {
        val hand = Hand()
        hand.add(DrawnCard(Card(CardSuit.SPADE, CardNumber.FIVE)))
        hand.add(DrawnCard(Card(CardSuit.HEART, CardNumber.SIX))) // 11
        hand.add(DrawnCard(Card(CardSuit.SPADE, CardNumber.ACE))) // 1

        assertThat(Score.of(hand).value).isEqualTo(12)
    }

    @DisplayName("카드 점수 테스트")
    @ParameterizedTest
    @CsvSource(
        delimiter = '=',
        value = [
            "(SPADE,ACE),(HEART,ACE)=12",
            "(SPADE,ACE),(HEART,TWO)=13",
            "(SPADE,ACE),(HEART,QUEEN)=21",
            "(SPADE,TWO),(HEART,FIVE)=7",
            "(SPADE,FIVE),(HEART,TEN)=15",
            "(SPADE,JACK),(HEART,QUEEN)=20",
            "(SPADE,ACE),(DIAMOND,ACE),(CLUB,ACE)=13",
            "(SPADE,ACE),(DIAMOND,TWO),(CLUB,TWO)=15",
            "(SPADE,ACE),(DIAMOND,ACE),(CLUB,QUEEN)=12",
            "(SPADE,ACE),(DIAMOND,TWO),(CLUB,QUEEN)=13",
            "(SPADE,ACE),(DIAMOND,TEN),(CLUB,QUEEN)=21",
            "(SPADE,TWO),(DIAMOND,TWO),(CLUB,TWO)=6",
            "(SPADE,TWO),(DIAMOND,TEN),(CLUB,QUEEN)=22",
            "(SPADE,ACE),(DIAMOND,ACE),(CLUB,ACE),(HEART,ACE)=14",
        ]
    )
    fun cardsScoreTest(inputCards: String, expectedScore: Int) {
        val cardRegex = Regex("""\(([A-Z]+),([A-Z]+)\)""")
        val hand = Hand()

        for (matchResult in cardRegex.findAll(inputCards)) {
            val (suit, number) = matchResult.destructured
            hand.add(DrawnCard(Card(CardSuit.valueOf(suit), CardNumber.valueOf(number))))
        }

        val score = Score.of(hand)

        assertThat(score.value).isEqualTo(expectedScore)
    }

    @DisplayName("CloseThan 테스트")
    @ParameterizedTest(name = "{0} 이 {1} 보다 21에 더 가깞다")
    @CsvSource(
        delimiter = ',',
        value = [
            "7,1",
            "10,1",
            "15,10",
            "20,10",
            "21,20",
        ]
    )
    fun scoreCloseThan(closer: Int, farther: Int) {
        assertThat(Score.of(closer).closerThan(Score.of(farther))).isTrue
    }

    @DisplayName("CloseThan 테스트")
    @ParameterizedTest(name = "{0} 은 {1} 보다 21에 가깝지 않다.")
    @CsvSource(
        delimiter = ',',
        value = [
            "15,15",
            "21,21",
            "22,10",
            "22,1",
        ]
    )
    fun scoreCloseThenFalseWhenEquals(farther: Int, closer: Int) {
        assertThat(Score.of(farther).closerThan(Score.of(closer))).isFalse
    }
}
