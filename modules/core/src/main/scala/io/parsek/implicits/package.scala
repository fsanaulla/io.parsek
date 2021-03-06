package io.parsek

/**
  * @author Andrei Tupitcyn
  */
package object implicits extends syntax.PValueSyntax
  with syntax.EncoderSyntax
  with syntax.TraversableSyntax
  with syntax.LensSyntax
  with instances.DecoderInstances
  with instances.EncoderInstances
  with instances.PMapInstances
