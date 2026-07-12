package com.valverdeball.createtablegames;

public final class ChessPiece {

  public static final byte EMPTY = 0;

  public static final byte PAWN = 1;
  public static final byte KNIGHT = 2;
  public static final byte BISHOP = 3;
  public static final byte ROOK = 4;
  public static final byte QUEEN = 5;
  public static final byte KING = 6;

  private ChessPiece() {}

  public static byte encode(byte pieceType, PlayerFaction.Side side) {
    if (side == PlayerFaction.Side.BLACK) {
      return (byte) -pieceType;
    }
    return (byte) pieceType;
  }

  public static byte type(byte square) {
    return (byte) Math.abs(square);
  }

  public static boolean isEmpty(byte square) {
    return square == EMPTY;
  }

  public static boolean isWhite(byte square) {
    return square > 0;
  }
  public static boolean isBlack(byte square) {
    return square < 0;
  }

  public static PlayerFaction.Side sideOf(byte square) {
    if(square > 0) return PlayerFaction.Side.WHITE;
    if(square < 0) return PlayerFaction.Side.BLACK;
    return PlayerFaction.Side.NONE;
  }
}