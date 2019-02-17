# Overview

This project is a Java based re-implementation of the venerable [LambdaMOO](https://en.wikipedia.org/wiki/LambdaMOO) program written by Pavel Curtis for Xerox PARC.
It is *not* a port of the original C code. Although that code base was referenced occasionally to clarify questions, 
the code present here was written primarily based on the _documentation_ for LambdaMOO; specifically the LambdaMOO Programmers Manual.
This _is_ a "derivitive work" and is subject to the legal requirements that imposes on it.

The intent of this re-implementation is to create a new, clean source base in a "modern" language. It makes use of the object 
orientated features of the language and is designed to be extensible and expandabale in a way the original was not.
As much as possible, the original design of LambdaMOO is kept to as strictly as possible. A lot of thought and experience
has gone into that design and that is important to creating a quality, stable and secure platform.

The one major departure from the original project is the scripting language. LambdaMOO used a purpose-specific language, MOO,
that users could use to instrument their virtual worlds. JavaLambdaMOO replaces this with Javascript. Although this precludes
using existing databases (inlcuding LambdaCore) directly with JavaLambdaMOO, it does greatly simplify the source base
(since the Nashorn Javascript engine can be used to handle the language) and provides a scripting interface that is likely
to be much more familiar to users without prior MOO experience. The vast majority of built-in commands are being ported
so that the logic of scripts should be the same between MOO and Javascript. Just the syntax will be different.

