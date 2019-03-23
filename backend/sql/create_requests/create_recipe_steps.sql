CREATE TABLE public.recipe_steps
(
  id_steps integer NOT NULL DEFAULT nextval('recipe_steps_id_steps_seq'::regclass),
  ord integer NOT NULL,
  description text NOT NULL,
  time_step integer NOT NULL,
  id_recipe integer NOT NULL,
  CONSTRAINT recipe_steps_pk PRIMARY KEY (id_steps)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.recipe_steps
  OWNER TO postgres;
