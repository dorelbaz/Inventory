
-- Database table.
create table Fooditems ( 
	name varchar(32) not null, 
	catalogue int check (catalogue > 0), 
	quantity int check (quantity >= 0), 
	production_date date not null, check (production_date < expiry_date), 
	expiry_date date not null, 
	min_temperature float check (min_temperature <= max_temperature),
	max_temperature float,
	price float check (price > 0),
	primary key(catalogue)
)

-- Insert examples.
INSERT INTO fooditems VALUES ('honey', 1010, 0, '1.1.2000', '1.1.2001', 10, 15, 7.5)
INSERT INTO fooditems VALUES ('milk', 1111, 10, '1.1.2000', '2.1.2000', 7, 12, 10.6)

-- Removes from the database items of 0 quantity and items whose name is a number. 
create or replace function trigf1() returns trigger as $$

declare q int;

begin
		select quantity
		from fooditems into q
		where new.catalogue = fooditems.catalogue;
		
		if (q = 0 or new.quantity = 0) then
			raise notice 'Quantity is 0.';
			delete from fooditems where fooditems.catalogue = new.catalogue;
			return null;
		else if (isnumeric(new.name) = true) then
			raise notice 'Name is numeric.';
			delete from fooditems where fooditems.catalogue = new.catalogue;
			return null;
		else
			raise notice 'Quantity is not 0 and name is not numeric.';
			return new;
	    end if;
		end if;
end; $$ language plpgsql


-- Trigger that fires after the table is updated or inserted into.
create trigger T1 
after insert or update on fooditems
for each row
execute procedure trigf1();

-- Checks whether a given text is composed soley of numbers.
CREATE OR REPLACE FUNCTION isnumeric(text) RETURNS BOOLEAN AS $$
DECLARE x NUMERIC;
BEGIN
    x = $1::NUMERIC;
    RETURN TRUE;
EXCEPTION WHEN others THEN
    RETURN FALSE;
END;
$$
STRICT
LANGUAGE plpgsql IMMUTABLE;